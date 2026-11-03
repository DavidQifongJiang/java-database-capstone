# Database schema design

MySQL database: `cms`. IDs use BIGINT auto-increment primary keys. Passwords contain BCrypt hashes and are excluded from JSON responses. All sample data is fictional.

## admin

| Column | SQL type | Constraints / meaning |
|---|---|---|
| id | BIGINT | Primary key, auto increment |
| username | VARCHAR(255) | Not null, unique login |
| password | VARCHAR(100) | Required password hash |

## doctor

| Column | SQL type | Constraints / meaning |
|---|---|---|
| id | BIGINT | Primary key, auto increment |
| name | VARCHAR(100) | Validated 3–100 characters |
| specialty | VARCHAR(50) | Validated 3–50 characters |
| email | VARCHAR(255) | Not null, unique, valid email |
| password | VARCHAR(100) | BCrypt hash, write-only JSON |
| phone | VARCHAR(255) | Validated exactly 10 digits |

`Doctor.availableTimes` is an `@ElementCollection List<String>`. Collection table `doctor_available_times` contains `doctor_id BIGINT NOT NULL` (foreign key to doctor.id) and `available_times VARCHAR(255)` (HH:mm - HH:mm). Service validation enforces unique one-hour slots.

`doctor_unavailable_dates` contains `doctor_id BIGINT NOT NULL` (foreign key to doctor.id) and `unavailable_date DATE`. The entity uses an `@ElementCollection Set<LocalDate>`.

## patient

| Column | SQL type | Constraints / meaning |
|---|---|---|
| id | BIGINT | Primary key, auto increment |
| name | VARCHAR(100) | Validated 3–100 characters |
| email | VARCHAR(255) | Not null, unique, valid email |
| password | VARCHAR(100) | BCrypt hash, write-only JSON |
| phone | VARCHAR(255) | Unique, validated exactly 10 digits |
| address | VARCHAR(255) | Required, at most 255 characters |

Repository queries support email login and duplicate detection by email OR phone.

## appointment

| Column | SQL type | Constraints / meaning |
|---|---|---|
| id | BIGINT | Primary key, auto increment |
| doctor_id | BIGINT | Not null foreign key to doctor.id; ManyToOne |
| patient_id | BIGINT | Not null foreign key to patient.id; ManyToOne |
| appointment_time | DATETIME(6) | Not null, Java LocalDateTime, @Future input validation |
| status | INT | 0 scheduled, 1 completed, validated range |

Unique `(doctor_id, appointment_time)` prevents duplicate slots. End time is derived as start plus one hour. Booking locks the doctor row and forces patient identity from the verified token. Only the owning patient or assigned doctor may access private appointment information.

## MongoDB prescriptions

Database/collection: `prescriptions` / `prescriptions`.

```json
{
  "_id": "MongoDB-generated identifier",
  "patientName": "Jordan Blake",
  "appointmentId": 1,
  "medication": "Demonstration-only item",
  "dosage": "Demo only",
  "doctorNotes": "Fictional course record; no treatment advice."
}
```

The Java model has String id, Long appointmentId, and validated String fields. MongoDB does not enforce a SQL foreign key: the service resolves the MySQL appointment and checks ownership. Patient name is copied from that appointment. One appointment can have multiple prescription documents.

## Reports

`GetDailyAppointmentReportByDoctor(date)` joins doctor, appointment and patient. `GetDoctorWithMostPatientsByMonth(month,year)` and `GetDoctorWithMostPatientsByYear(year)` count patient visits per doctor, with deterministic tie ordering. `GetAppointmentCountByMonth(year)` supports administrator statistics. Counts are appointment rows, including repeat visits, not distinct people.
