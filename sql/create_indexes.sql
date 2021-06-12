DROP INDEX IF EXISTS Patient_patient_id_index;
DROP INDEX IF EXISTS Patient_name_index;
DROP INDEX IF EXISTS Patient_gtype_index;
DROP INDEX IF EXISTS Patient_age_index;
DROP INDEX IF EXISTS Patient_address_index;
DROP INDEX IF EXISTS Patient_number_of_appts_index;
DROP INDEX IF EXISTS Department_dept_ID_index;
DROP INDEX IF EXISTS Department_name_index;
DROP INDEX IF EXISTS Department_hid_index;
DROP INDEX IF EXISTS Doctor_doctor_ID_index;
DROP INDEX IF EXISTS Doctor_name_index;
DROP INDEX IF EXISTS Doctor_specialty_index;
DROP INDEX IF EXISTS Doctor_did_index;
DROP INDEX IF EXISTS Appointment_appnt_ID_index;
DROP INDEX IF EXISTS Appointment_adate_index;
DROP INDEX IF EXISTS Appointment_time_slot_index;
DROP INDEX IF EXISTS Appointment_status_index;
DROP INDEX IF EXISTS has_appointment_appt_id_index;
DROP INDEX IF EXISTS has_appointment_doctor_id_index;

CREATE INDEX Patient_patient_id_index
ON Patient
USING BTREE (patient_id);
CREATE INDEX Patient_name_index
ON Patient
USING BTREE (name);
CREATE INDEX Patient_gtype_index
ON Patient
USING BTREE (gtype);
CREATE INDEX Patient_age_index
ON Patient
USING BTREE (age);
CREATE INDEX Patient_address_index
ON Patient
USING BTREE (address);
CREATE INDEX Patient_number_of_appts__index
ON Patient
USING BTREE (number_of_appts);

CREATE INDEX Department_dept_ID_index
ON Department
USING BTREE (dept_ID);
CREATE INDEX Department_name_index
ON Department
USING BTREE (name);
CREATE INDEX Department_hid_index
ON Department
USING BTREE (hid);

CREATE INDEX Doctor_doctor_ID_index
ON Doctor
USING BTREE (doctor_ID);
CREATE INDEX Doctor_name_index
ON Doctor
USING BTREE (name);
CREATE INDEX Doctor_specialty_index
ON Doctor
USING BTREE (specialty);
CREATE INDEX Doctor_did_index
ON Doctor
USING BTREE (did);

CREATE INDEX Appointment_appnt_ID_index
ON Appointment
USING BTREE (appnt_ID);
CREATE INDEX Appointment_adate_index
ON Appointment
USING BTREE (adate);
CREATE INDEX Appointment_time_slot_index
ON Appointment
USING BTREE (time_slot);
CREATE INDEX Appointment_status_index
ON Appointment
USING BTREE (status);

CREATE INDEX has_appointment_appt_id_index
ON has_appointment
USING (appt_id);
CREATE INDEX has_appointment_doctor_id_index
ON has_appointment
USING (doctor_id);
