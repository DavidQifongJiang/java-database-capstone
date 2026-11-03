# Smart Clinic user stories

17 stories: five administrator, six patient and six doctor.

## As a admin, I want to sign in securely

As a admin, I want to sign in securely, so that I can manage clinic operations.

### Acceptance criteria

- [ ] Valid credentials open the administrator dashboard.
- [ ] Invalid credentials return a generic error.
- [ ] Other roles cannot call administrator endpoints.

Priority: High
Story points: 3

Notes: IBM Smart Clinic capstone. Use fictional demonstration data only. Implementation and validation are in this repository.


## As a admin, I want to sign out

As a admin, I want to sign out, so that I can end my browser session.

### Acceptance criteria

- [ ] Sign-out clears browser token storage.
- [ ] The MVC session cookie is expired.
- [ ] Returning to a protected dashboard requires sign-in.

Priority: High
Story points: 2

Notes: IBM Smart Clinic capstone. Use fictional demonstration data only. Implementation and validation are in this repository.


## As a admin, I want to add a doctor

As a admin, I want to add a doctor, so that I can expand the searchable care team.

### Acceptance criteria

- [ ] Required profile fields and one-hour slots are validated.
- [ ] Duplicate email addresses are rejected.
- [ ] The saved doctor appears in directory search.

Priority: High
Story points: 5

Notes: IBM Smart Clinic capstone. Use fictional demonstration data only. Implementation and validation are in this repository.


## As a admin, I want to remove a doctor profile

As a admin, I want to remove a doctor profile, so that I can keep the directory current.

### Acceptance criteria

- [ ] Only an administrator can remove a profile.
- [ ] The UI asks before removing a fictional demo doctor.
- [ ] The directory refreshes and associated appointments are removed.

Priority: High
Story points: 3

Notes: IBM Smart Clinic capstone. Use fictional demonstration data only. Implementation and validation are in this repository.


## As a admin, I want to query monthly appointment statistics in MySQL

As a admin, I want to query monthly appointment statistics in MySQL, so that I can review clinic activity.

### Acceptance criteria

- [ ] GetAppointmentCountByMonth accepts a year.
- [ ] Results group appointment totals by month.
- [ ] The CLI can execute reports without exposing database passwords.

Priority: High
Story points: 3

Notes: IBM Smart Clinic capstone. Use fictional demonstration data only. Implementation and validation are in this repository.


## As a patient, I want to view available doctors without signing in

As a patient, I want to view available doctors without signing in, so that I can choose a care provider.

### Acceptance criteria

- [ ] The public page lists doctor names and specialties.
- [ ] Search filters by name, specialty and AM/PM slots.
- [ ] Public results never include password hashes.

Priority: High
Story points: 3

Notes: IBM Smart Clinic capstone. Use fictional demonstration data only. Implementation and validation are in this repository.


## As a patient, I want to register an account

As a patient, I want to register an account, so that I can manage my appointments.

### Acceptance criteria

- [ ] Name, email, phone, address and password are validated.
- [ ] Duplicate email or phone is rejected.
- [ ] The stored password is BCrypt hashed.

Priority: High
Story points: 3

Notes: IBM Smart Clinic capstone. Use fictional demonstration data only. Implementation and validation are in this repository.


## As a patient, I want to sign in

As a patient, I want to sign in, so that I can access my private appointments.

### Acceptance criteria

- [ ] Valid email/password returns a patient role token.
- [ ] Invalid credentials return a generic error.
- [ ] The patient dashboard displays the signed-in name.

Priority: High
Story points: 3

Notes: IBM Smart Clinic capstone. Use fictional demonstration data only. Implementation and validation are in this repository.


## As a patient, I want to sign out

As a patient, I want to sign out, so that I can end access from my browser.

### Acceptance criteria

- [ ] Browser token storage is cleared.
- [ ] The session cookie is expired.
- [ ] Protected pages require signing in again.

Priority: High
Story points: 2

Notes: IBM Smart Clinic capstone. Use fictional demonstration data only. Implementation and validation are in this repository.


## As a patient, I want to book a one-hour appointment

As a patient, I want to book a one-hour appointment, so that I can reserve a convenient available slot.

### Acceptance criteria

- [ ] Only future configured slots are bookable.
- [ ] Concurrent requests cannot book the same doctor/time twice.
- [ ] The patient identity comes from the authenticated token.

Priority: High
Story points: 5

Notes: IBM Smart Clinic capstone. Use fictional demonstration data only. Implementation and validation are in this repository.


## As a patient, I want to view my upcoming appointments

As a patient, I want to view my upcoming appointments, so that I can track my schedule and records.

### Acceptance criteria

- [ ] The list contains only my appointments.
- [ ] Each row shows doctor, date/time and status.
- [ ] I can cancel my future scheduled appointment and view my records.

Priority: High
Story points: 5

Notes: IBM Smart Clinic capstone. Use fictional demonstration data only. Implementation and validation are in this repository.


## As a doctor, I want to sign in

As a doctor, I want to sign in, so that I can access my own clinical workspace.

### Acceptance criteria

- [ ] Valid credentials issue a doctor role token.
- [ ] The dashboard shows the doctor name.
- [ ] Invalid credentials are rejected.

Priority: High
Story points: 3

Notes: IBM Smart Clinic capstone. Use fictional demonstration data only. Implementation and validation are in this repository.


## As a doctor, I want to sign out

As a doctor, I want to sign out, so that I can end my browser session.

### Acceptance criteria

- [ ] Stored browser tokens are cleared.
- [ ] The MVC cookie is expired.
- [ ] A protected dashboard requires authentication again.

Priority: High
Story points: 2

Notes: IBM Smart Clinic capstone. Use fictional demonstration data only. Implementation and validation are in this repository.


## As a doctor, I want to view my appointment calendar

As a doctor, I want to view my appointment calendar, so that I can prepare for scheduled visits.

### Acceptance criteria

- [ ] The calendar accepts a date.
- [ ] Results include only appointments assigned to me.
- [ ] Patient-name filtering narrows that date safely.

Priority: High
Story points: 5

Notes: IBM Smart Clinic capstone. Use fictional demonstration data only. Implementation and validation are in this repository.


## As a doctor, I want to mark a date unavailable

As a doctor, I want to mark a date unavailable, so that I can prevent new appointments during time off.

### Acceptance criteria

- [ ] Only my own unavailable dates can be updated.
- [ ] Marked dates return no bookable slots.
- [ ] Removing the unavailable mark restores otherwise free slots.

Priority: High
Story points: 3

Notes: IBM Smart Clinic capstone. Use fictional demonstration data only. Implementation and validation are in this repository.


## As a doctor, I want to update my profile and availability

As a doctor, I want to update my profile and availability, so that I can keep my directory information accurate.

### Acceptance criteria

- [ ] Name, specialty, email, phone and slots are editable.
- [ ] Validation rejects invalid or duplicate slots.
- [ ] The token identity determines the profile being updated.

Priority: High
Story points: 5

Notes: IBM Smart Clinic capstone. Use fictional demonstration data only. Implementation and validation are in this repository.


## As a doctor, I want to view patient details for assigned appointments and add records

As a doctor, I want to view patient details for assigned appointments and add records, so that I can maintain visit context.

### Acceptance criteria

- [ ] Only assigned appointment details are accessible.
- [ ] Validated prescription records persist in MongoDB.
- [ ] The owning patient can read the resulting record.

Priority: High
Story points: 5

Notes: IBM Smart Clinic capstone. Use fictional demonstration data only. Implementation and validation are in this repository.
