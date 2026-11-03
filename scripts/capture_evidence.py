"""Capture real local demo SQL/curl results; credentials/tokens are never printed."""
from pathlib import Path
import datetime as dt
import json
import subprocess
from smoke_test import login

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / 'evidence'
OUT.mkdir(exist_ok=True)

def sql(query, table=True):
    args = ['docker', 'compose', 'exec', '-T', 'mysql', 'sh', '-c',
            'MYSQL_PWD="$MYSQL_PASSWORD" mysql -u clinic cms ' + ('--table' if table else '--batch --skip-column-names')]
    return subprocess.run(args, input=query, text=True, cwd=ROOT, capture_output=True, check=True).stdout

def save(name, command, output):
    (OUT / name).write_text(command + '\n\n' + output, encoding='utf-8')
    print(name + ': saved actual command output')

if __name__ == '__main__':
    day = sql('SELECT DATE(MIN(appointment_time)) FROM appointment;', False).strip()
    date = dt.date.fromisoformat(day)
    queries = {
        'show-tables.txt': 'SHOW TABLES;',
        'patients.txt': 'SELECT id,name,email,phone,address FROM patient ORDER BY id;',
        'daily-report.txt': f"CALL GetDailyAppointmentReportByDoctor('{day}');",
        'monthly-report.txt': f'CALL GetDoctorWithMostPatientsByMonth({date.month},{date.year});',
        'yearly-report.txt': f'CALL GetDoctorWithMostPatientsByYear({date.year});',
        'schema-actual.txt': 'SHOW CREATE TABLE admin; SHOW CREATE TABLE doctor; SHOW CREATE TABLE patient; SHOW CREATE TABLE appointment;'
    }
    for filename, query in queries.items():
        save(filename, 'mysql> ' + query, sql(query))
    token = login('patient', 'patient1@example.test')
    urls = {
        'curl-doctors.txt': ('http://127.0.0.1:8088/doctor', False),
        'curl-patient-appointments.txt': ('http://127.0.0.1:8088/patient/appointments', True),
        'curl-doctor-filter.txt': ('http://127.0.0.1:8088/doctor/filter?specialty=Cardiology&time=AM', False)
    }
    for filename, (url, authenticated) in urls.items():
        args = ['curl.exe', '--fail-with-body', '--silent', '--show-error', url]
        command = f'curl --fail-with-body --silent "{url}"'
        if authenticated:
            args += ['--header', 'Authorization: Bearer ' + token]
            command = 'After POST /patient/login with the fictional patient account (token omitted):\n' + command + ' -H "Authorization: Bearer <login-token>"'
        result = subprocess.run(args, capture_output=True, text=True, check=True)
        save(filename, command, json.dumps(json.loads(result.stdout), indent=2) + '\n')
