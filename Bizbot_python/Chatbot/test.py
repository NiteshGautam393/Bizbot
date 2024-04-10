# import sqlite3
# import random
#
# # Create a connection to the SQLite database
# conn = sqlite3.connect('orders.db')
# cursor = conn.cursor()
#
#
#
#
# cursor.execute('''
#                 INSERT INTO products (product_id, type, stock, price, sizes ,colors)
#                 VALUES (?, ?, ?, ?, ?, ?)
#             ''', (12333, 'Nike rack', 100, 1700, 'L,S,M','Blue, Black, Green'))
#
# conn.commit()
# conn.close()
import mysql.connector

# Connect to the MySQL server
conn = mysql.connector.connect(
    host='localhost',
    user='root',
    password='Bizbot@9067',
    database='bizbot'
)

order_id = 1427
cursor = conn.cursor()
cursor.execute('''
    SELECT o.OrderNumber, o.productID, o.Phone, o.orderStatus,
           o.Quantity, o.Size, o.Colors
    FROM testorder o
    WHERE o.orderNumber = %(order_id)s
''', {'order_id': int(order_id)})

result = cursor.fetchall()
print(result)



