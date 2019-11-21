CREATE TABLE governance_metadata(
database_name string,
table_name string,
column_name string,
description string,
owner string,
last_updated_data string,
source_system string,
personal_data string,
PRIMARY KEY (database_name, table_name, column_name)
)
STORED AS kudu;
