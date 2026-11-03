"""Exercise the running demo through HTTP; never print authentication tokens."""
import concurrent.futures
import datetime as dt
import json
import urllib.request
import urllib.error

BASE = 'http://127.0.0.1:8088'
def call(path, method='GET', data=None, token=None):
    headers = {'Content-Type': 'application/json'}
    if token:
        headers['Authorization'] = 'Bearer ' + token
    request = urllib.request.Request(BASE + path, data=json.dumps(data).encode() if data is not None else None, headers=headers, method=method)
    try:
        with urllib.request.urlopen(request, timeout=20) as response:
            return response.status, json.loads(response.read() or b'{}')
    except urllib.error.HTTPError as error:
        return error.code, json.loads(error.read() or b'{}')

def login(role, email):
    status, body = call('/' + role + '/login', 'POST', {'email': email, 'password': 'DemoPassword1!'})
    assert status == 200, (status, body)
    return body['token']

if __name__ == '__main__':
    patient = login('patient', 'patient1@example.test')
    other = login('patient', 'patient2@example.test')
    doctor = login('doctor', 'avery.chen@example.test')
    admin = login('admin', 'admin')
    print('PASS: all three roles authenticate')
    assert call('/patient/appointments')[0] == 401
    assert call('/patient/appointments', token=doctor)[0] == 403
    assert call('/patient/appointments', token=patient + 'tampered')[0] == 401
    assert call('/patient/login', 'POST', {'email': 'patient1@example.test', 'password': 'incorrect'})[0] == 401
    print('PASS: missing, tampered and wrong-role tokens rejected; bad password rejected')
    status, listed = call('/doctor')
    assert status == 200 and len(listed['doctors']) >= 3
    assert all('password' not in item for item in listed['doctors'])
    status, filtered = call('/doctor/filter?specialty=Cardiology&time=AM')
    assert status == 200 and filtered['doctors'][0]['name'] == 'Avery Chen'
    print('PASS: doctor listing/filtering omit password hashes')
    tomorrow = (dt.date.today() + dt.timedelta(days=1)).isoformat()
    status, appointments = call('/patient/appointments', token=patient)
    own = appointments['appointments'][0]
    assert status == 200 and own['patient']['email'] == 'patient1@example.test'
    assert 'password' not in own['patient'] and 'password' not in own['doctor']
    assert call('/appointments/' + str(own['id']), 'DELETE', token=other)[0] == 403
    assert call('/prescription/' + str(own['id']) + '?role=patient', token=other)[0] == 403
    print('PASS: patients cannot cancel or read another patient appointment')
    status, schedule = call('/appointments?date=' + tomorrow, token=doctor)
    assert status == 200 and len(schedule['appointments']) >= 3
    payload = {'patientName': 'Fictional Demo', 'appointmentId': own['id'], 'medication': 'Demonstration-only item', 'dosage': 'Demo only', 'doctorNotes': 'Fictional record for software testing; no treatment advice.'}
    assert call('/prescription', 'POST', payload, patient)[0] == 403
    assert call('/prescription', 'POST', payload, doctor)[0] in (200, 201)
    status, prescriptions = call('/prescription/' + str(own['id']) + '?role=patient', token=patient)
    assert status == 200 and prescriptions['prescriptions'][-1]['patientName'] == own['patient']['name']
    print('PASS: authorized doctor writes MongoDB record; owning patient can read it')
    booking_day = (dt.date.today() + dt.timedelta(days=3)).isoformat()
    status, available = call('/doctor/availability/patient/3/' + booking_day, token=patient)
    assert status == 200 and available['availableTimes']
    slot = available['availableTimes'][0].split('-')[0].strip()
    booking = {'doctor': {'id': 3}, 'patient': {'id': 999999}, 'appointmentTime': booking_day + 'T' + slot + ':00', 'status': 1}
    with concurrent.futures.ThreadPoolExecutor(max_workers=2) as pool:
        results = list(pool.map(lambda token: call('/appointments', 'POST', booking, token), [patient, other]))
    statuses = sorted(result[0] for result in results)
    assert statuses == [201, 409], results
    winner_index = next(i for i, result in enumerate(results) if result[0] == 201)
    winner = [patient, other][winner_index]
    status, booked = call('/patient/appointments', token=winner)
    created = next(a for a in booked['appointments'] if a['appointmentTime'].startswith(booking_day))
    assert created['patient']['id'] != 999999 and created['status'] == 0
    assert call('/appointments/' + str(created['id']), 'DELETE', token=winner)[0] in (200, 204)
    print('PASS: concurrent requests create one booking; identity/status forced; test booking cancelled')
    print('All HTTP smoke checks passed against MySQL and MongoDB.')
