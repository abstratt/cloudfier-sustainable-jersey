INSERT INTO service_requests.Profile (id, username, password) VALUES (1, 'admin', 'pass');
INSERT INTO service_requests.Profile (id, username, password) VALUES (2, 'ikant', 'pass');
INSERT INTO service_requests.Profile (id, username, password) VALUES (3, 'jlocke', 'pass');
INSERT INTO service_requests.Profile (id, username, password) VALUES (4, 'slanger', 'pass');
INSERT INTO service_requests.Profile (id, username, password) VALUES (5, 'kmarx', 'pass');

ALTER SEQUENCE service_requests.profile_id_seq RESTART WITH 6;

ALTER SEQUENCE service_requests.servicerequest_id_seq RESTART WITH 5;

ALTER SEQUENCE service_requests.person_id_seq RESTART WITH 6;

ALTER SEQUENCE service_requests.servicerequesttype_id_seq RESTART WITH 7;

INSERT INTO service_requests.Person (id, firstName, lastName) VALUES (1, 'System', 'Administrator');
INSERT INTO service_requests.SystemAdministrator (id, userProfile_id) VALUES (1, 1);

INSERT INTO service_requests.Person (id, firstName, lastName) VALUES (2, 'Immanuel', 'Kant');
INSERT INTO service_requests.CityOfficial (id, userProfile_id) VALUES (2, 2);

INSERT INTO service_requests.Person (id, firstName, lastName) VALUES (3, 'John', 'Locke');
INSERT INTO service_requests.Resident (id, userProfile_id) VALUES (3, 3);
INSERT INTO service_requests.Person (id, firstName, lastName) VALUES (4, 'Susanne', 'Langer');
INSERT INTO service_requests.Resident (id, userProfile_id) VALUES (4, 4);
INSERT INTO service_requests.Person (id, firstName, lastName) VALUES (5, 'Karl', 'Marx');
INSERT INTO service_requests.Resident (id, userProfile_id) VALUES (5, 5);

INSERT INTO service_requests.ServiceRequestType (id, name) VALUES (1, 'Pothole');
INSERT INTO service_requests.ServiceRequestType (id, name) VALUES (2, 'Litter');
INSERT INTO service_requests.ServiceRequestType (id, name) VALUES (3, 'MIssing sign');
INSERT INTO service_requests.ServiceRequestType (id, name) VALUES (4, 'Blocked storm drain');
INSERT INTO service_requests.ServiceRequestType (id, name) VALUES (5, 'Illegal dumping');
INSERT INTO service_requests.ServiceRequestType (id, name) VALUES (6, 'Graffiti');

INSERT INTO service_requests.ServiceRequest (id, reportDate, description, status, picture, location, acceptanceDate, completionDate, staffComment, serviceRequestType_id, resident_id) VALUES (1, DATE '2017-03-22', 'Near to intersection with Howard St', 'Accepted', null, '+39.481410,-75.011385', null, null, '', 1, 4);
INSERT INTO service_requests.ServiceRequest (id, reportDate, description, status, picture, location, acceptanceDate, completionDate, staffComment, serviceRequestType_id, resident_id) VALUES (2, DATE '2017-03-22', 'Landis Junior High,', 'Invalid', null, null, null, null, 'already addressed, thank you', 6, 5);
INSERT INTO service_requests.ServiceRequest (id, reportDate, description, status, picture, location, acceptanceDate, completionDate, staffComment, serviceRequestType_id, resident_id) VALUES (3, DATE '2017-03-22', 'Most on Stafford Dr', 'Submitted', null, null, null, null, '', 4, 5);
INSERT INTO service_requests.ServiceRequest (id, reportDate, description, status, picture, location, acceptanceDate, completionDate, staffComment, serviceRequestType_id, resident_id) VALUES (4, DATE '2017-03-22', 'Nearby Landis Jr High school parking lot', 'Submitted', null, null, null, null, '', 2, 3);

ALTER SEQUENCE service_requests.profile_id_seq RESTART WITH 6;

ALTER SEQUENCE service_requests.servicerequest_id_seq RESTART WITH 5;

ALTER SEQUENCE service_requests.person_id_seq RESTART WITH 6;

ALTER SEQUENCE service_requests.servicerequesttype_id_seq RESTART WITH 7;
