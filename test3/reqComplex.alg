users: users.csv
orders: orders.csv

R1 = SELECT users age < 28
R2 = PROJ R1 id name city
R3 = JOIN R2 orders id user_id
R = PROJ R3 name city amount