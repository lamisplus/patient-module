INSERT INTO permission (id, description, name, date_created, created_by, date_modified, modified_by, archived,
                        module_name)
VALUES ((SELECT MAX(id) + 1 FROM permission), 'create patient', 'create_patient', current_timestamp, 'SYSTEM',
        current_timestamp, 'SYSTEM', 0, 'PatientModule');
INSERT INTO permission (id, description, name, date_created, created_by, date_modified, modified_by, archived,
                        module_name)
VALUES ((SELECT MAX(id) + 1 FROM permission), 'edit patient', 'edit_patient', current_timestamp, 'SYSTEM',
        current_timestamp, 'SYSTEM', 0, 'PatientModule');
INSERT INTO permission (id, description, name, date_created, created_by, date_modified, modified_by, archived,
                        module_name)
VALUES ((SELECT MAX(id) + 1 FROM permission), 'view patient', 'view_patient', current_timestamp, 'SYSTEM',
        current_timestamp, 'SYSTEM', 0, 'PatientModule');
INSERT INTO permission (id, description, name, date_created, created_by, date_modified, modified_by, archived,
                        module_name)
VALUES ((SELECT MAX(id) + 1 FROM permission), 'delete patient', 'delete_patient', current_timestamp, 'SYSTEM',
        current_timestamp, 'SYSTEM', 0, 'PatientModule');
INSERT INTO permission (id, description, name, date_created, created_by, date_modified, modified_by, archived,
                        module_name)
VALUES ((SELECT MAX(id) + 1 FROM permission), 'patient check in', 'patient_check_in', current_timestamp, 'SYSTEM',
        current_timestamp, 'SYSTEM', 0, 'PatientModule');
INSERT INTO permission (id, description, name, date_created, created_by, date_modified, modified_by, archived,
                        module_name)
VALUES ((SELECT MAX(id) + 1 FROM permission), 'patient biometric capture', 'patient_biometric_capture',
        current_timestamp, 'SYSTEM', current_timestamp,
        'SYSTEM', 0, 'PatientModule');
INSERT INTO permission (id, description, name, date_created, created_by, date_modified, modified_by, archived,
                        module_name)
VALUES ((SELECT MAX(id) + 1 FROM permission), 'create patient appointment', 'create_patient_appointment',
        current_timestamp, 'SYSTEM', current_timestamp,
        'SYSTEM', 0, 'PatientModule');
INSERT INTO permission (id, description, name, date_created, created_by, date_modified, modified_by, archived,
                        module_name)
VALUES ((SELECT MAX(id) + 1 FROM permission), 'view patient appointment', 'view_patient_appointment', current_timestamp,
        'SYSTEM', current_timestamp,
        'SYSTEM', 0, 'PatientModule');
