import datetime
import logging
import sqlite3
import mysql.connector

from fastapi import FastAPI, Request
from fastapi import HTTPException
from fastapi.responses import JSONResponse
from pydantic import BaseModel

app = FastAPI()


# Define Pydantic models for request validation
class QueryResult(BaseModel):
    intent: dict
    parameters: dict


class Payload(BaseModel):
    queryResult: QueryResult


logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

context_prod = None
context_color = None
context_size = None
context_quantity = None
context_phone = None
context_contact = None
context_id = None
import mysql.connector

global cnx


def connect_to_db():
    global cnx
    cnx = mysql.connector.connect(
        host='localhost',
        user='root',
        password='Bizbot@9067',
        database='bizbot'
    )
    return cnx, cnx.cursor()


# DONE
def get_order_details(order_id, cursor):
    try:

        cursor.execute('''
            SELECT o.OrderNumber, o.productID, o.Phone, o.orderStatus,
                   o.Quantity, o.Size, o.Colors
            FROM testorder o
            WHERE o.orderNumber = %(order_id)s
        ''', {'order_id': int(order_id)})

        result = cursor.fetchall()
        logger.info(result)
        return result

    except sqlite3.Error as e:
        # Handle database-related errors
        print(f"Error retrieving order details: {str(e)}")
        return None


async def handle_order_track(intent_name, parameters, cursor):
    try:
        order_id = parameters.get('number')
        logger.info(f"Received Intent: {intent_name}")
        logger.info(f"Received Parameters: {parameters}")
        logger.info(f"Order ID: {order_id}")

        if not order_id:
            raise ValueError("Order ID not provided")

        # Retrieve order details from the database
        order_details = get_order_details(int(order_id), cursor)
        logger.info(order_details)

        if not order_details:
            raise ValueError(f"Order with ID {order_id} not found")

        elements = [
            {
                "title": f"Order ID {int(order_id)} : {order_details[0][4]}",
                "subtitle": f"{order_details[0][3]}",
            }
        ]

        for product in order_details:
            elements.append({
                "title": f"{product[1]} - Quantity: {product[4]}",  # Adjust indices based on your tuple structure
                "subtitle": f"Size: {product[-2]}, Colors: {product[-1]}",
            })

        fulfillment_message = {
            "fulfillmentMessages": [
                {
                    "payload": {
                        "facebook": {
                            "attachment": {
                                "type": "template",
                                "payload": {
                                    "template_type": "generic",
                                    "elements": elements
                                }
                            }
                        }
                    },
                    "platform": "FACEBOOK"
                }
            ]
        }

        # Log the response
        logger.info(f"Generated Response: {fulfillment_message}")

        # Return fulfillment message
        return JSONResponse(content=fulfillment_message)

    except ValueError as ve:
        # Handle specific data errors
        logger.error(f"ValueError in handle_order_track: {str(ve)}", exc_info=True)
        raise HTTPException(status_code=404, detail=f"Order not found: {str(ve)}")

    except sqlite3.Error as e:
        # Handle database-related errors
        logger.error(f"Error in handle_order_track: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail="Internal Server Error")

    except Exception as e:
        # Log the unexpected exception
        logger.error(f"Unexpected error in handle_order_track: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail="Internal Server Error")


async def handle_retrieve_prod_info(intent_name, parameters, cursor):
    global context_prod

    try:
        prod_id = int(parameters.get('prod'))
        context_prod = prod_id
        logger.info(f"Received Intent: {intent_name}")
        logger.info(f"Received Parameters: {parameters}")
        logger.info(f"Product ID: {prod_id}")

        if not prod_id:
            raise ValueError("Product ID not provided")

        # Existing code
        cursor.execute("SELECT * FROM product WHERE product_id = %s", (prod_id,))
        product_info = cursor.fetchone()
        logger.info(product_info)
        # Modify this part
        if not product_info:
            fulfillment_message = {
                "fulfillmentMessages": [
                    {
                        "text": {
                            "text": ["Product ID does not match. Please try again carefully."]
                        }
                    }
                ]
            }
        else:
            # Access tuple elements using integer indices
            title = f"{product_info[2]} - {product_info[4]}"  # Assuming 'type' is at index 1, and 'name' is at index 2
            subtitle = f"Price: {product_info[3]}\nSizes: {product_info[7]}\n Colors: {product_info[8]}"

            # Construct the fulfillment message with product attributes
            fulfillment_message = {
                "fulfillmentMessages": [
                    {
                        "payload": {
                            "facebook": {
                                "attachment": {
                                    "type": "image",
                                    "payload": {
                                        "url": "https://i.ibb.co/M6QpTq0/mens-sizes.jpg"
                                        # Replace with the actual path to your image file
                                    }
                                }
                            }
                        },
                        "platform": "FACEBOOK"
                    },
                    {
                        "payload": {
                            "facebook": {
                                "attachment": {
                                    "type": "template",
                                    "payload": {
                                        "template_type": "generic",
                                        "elements": [
                                            {
                                                "title": title,
                                                "subtitle": subtitle,
                                            }
                                        ]
                                    }
                                }
                            }
                        },
                        "platform": "FACEBOOK"
                    },
                    {
                        "quickReplies": {
                            "title": "Would you like to order this product?",
                            "quickReplies": ["Yea, sure", "No thanks."]
                        },
                        "platform": "FACEBOOK"
                    }
                ],
                "outputContexts": [
                    {
                        "name": "projects/newagent-cvrj/locations/global/agent/sessions/your-session-id/contexts/order_confirmation",
                        "lifespanCount": 2,
                        "parameters": {
                            "prod_id": prod_id
                        }
                    }
                ]
            }

        # Log the response
        logger.info(f"Generated Response: {fulfillment_message}")

        # Return fulfillment message
        return JSONResponse(content=fulfillment_message)

    except (ValueError, TypeError) as e:
        # Handle specific data parsing errors
        logger.error(f"Error in handle_retrieve_prod_info: {str(e)}", exc_info=True)
        raise HTTPException(status_code=400, detail=f"Invalid data in payload: {str(e)}")

    except Exception as e:
        # Log the unexpected exception
        logger.error(f"Unexpected error in handle_retrieve_prod_info: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail="Internal Server Error")


async def handle_order_query_retrieve_prod_info_yes(intent_name, parameters):
    global context_prod
    logger.info(context_prod)

    fulfillment_message = {
        "fulfillmentMessages": [
            {
                "text": {
                    "text": [
                        "Please mention your prefered quantity, size and color"
                    ]
                }
            }
        ]
    }

    logger.info(fulfillment_message)

    return JSONResponse(content=fulfillment_message)


async def add_items(intent_name, parameters):
    '''
    :param intent_name: to add item
    :param parameters: quantity,size,color
    :return: add quantity,size,color to their global variables
    '''
    global context_size
    global context_color
    global context_quantity

    try:
        # Extract relevant information from the parameters
        context_quantity = parameters.get('number')
        context_color = parameters.get('color')
        context_size = parameters.get('size')

        fulfillment_message = {
            "fulfillmentMessages": [
                {
                    "text": {
                        "text": [
                            "Anything else or should i conform this?"
                        ]
                    }
                }
            ]
        }
        return JSONResponse(content=fulfillment_message)

    except ValueError as e:
        # Handle specific errors
        logger.error(f"Error handling retrieve_cust_info: {str(e)}")
        raise HTTPException(status_code=400, detail=str(e))

    except Exception as e:
        # Log unexpected errors
        logger.error(f"Unexpected error handling retrieve_cust_info: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail="Internal Server Error")


async def handle_retrieve_cust_info(intent_name, parameters, cursor):
    '''
    :param intent_name: intent name
    :param parameters: phone_number
    :return: Unique order id to user, saves order, saves order_product, changes stock of product, end of convo
    '''

    global context_phone
    global context_size
    global context_color
    global context_quantity
    global context_prod


    # NO need to send global variables
    async def insert_order(order_id, phone, status, context_product, context_quantity, context_color,
                           context_size, cursor):
        try:
            if len(phone) != 10:
                fulfillment_message = {
                    "fulfillmentMessages": [
                        {
                            "text": {
                                "text": [
                                    "Phone number should be of 10 digits."
                                ]
                            }
                        }
                    ]
                }
                return JSONResponse(content=fulfillment_message)

            # Insert the order information into the 'orders' table
            cursor.execute('''
                INSERT INTO testorder (OrderNumber, orderDate, Phone, orderStatus, productID, Quantity, Colors, Size)
                VALUES (%(order_id)s, %(order_date)s, %(phone)s, %(status)s,%(context_product)s, %(context_quantity)s,
                 %(context_color)s, %(context_size)s)
            ''', {
                'order_id': order_id,
                'order_date': datetime.datetime.now().date(),
                'phone': phone,
                'status': status,
                'context_product': context_product,
                'context_quantity': context_quantity,
                'context_color': context_color,
                'context_size': context_size
            })


            # cursor.execute('''
            #     UPDATE product
            #     SET stock = stock - %(context_quantity)s
            #     WHERE product_id = %(context_product)s
            # ''', {
            #     'context_quantity': context_quantity,
            #     'context_product': context_product
            # })


        except Exception as e:
            # Handle database-related errors
            logger.error(f"Error inserting order into database: {str(e)}")
            raise HTTPException(status_code=500, detail="Internal Server Error")

    if context_quantity is not None:
        try:
            # Extract relevant information from the parameters
            context_phone = parameters.get('phone-number')

            logger.info(f"Customer Phone Number: {context_phone}")
            logger.info(f"Customer Quantity: {context_quantity}")
            logger.info(f"Customer Size: {context_size}")
            logger.info(f"Customer Color: {context_color}")

            import random
            order_id = f'{random.randint(1000, 9999)}'
            # Check if the phone number is of valid length
            if len(context_phone) != 10:
                fulfillment_message = {
                    "fulfillmentMessages": [
                        {
                            "text": {
                                "text": [
                                    "Phone number should be of 10 digits."
                                ]
                            }
                        }
                    ]
                }
                return JSONResponse(content=fulfillment_message)

            # Insert order into the 'orders' table
            await insert_order(order_id, context_phone, "Initiated", context_prod, context_quantity, context_color,
                               context_size, cursor)

            fulfillment_message = {
                "fulfillmentMessages": [
                    {
                        "text": {
                            "text": [
                                f"Your order has been confirmed {order_id}. Please keep this safe."
                            ]
                        }
                    }
                ]
            }
            logger.info(fulfillment_message)
            return JSONResponse(content=fulfillment_message)

        except ValueError as e:
            # Handle specific errors
            logger.error(f"Error handling retrieve_cust_info: {str(e)}")
            raise HTTPException(status_code=400, detail=str(e))

        except Exception as e:
            # Log unexpected errors
            logger.error(f"Unexpected error handling retrieve_cust_info: {str(e)}", exc_info=True)
            raise HTTPException(status_code=500, detail="Internal Server Error")  # ##

    else:
        fulfillment_message = {
            "fulfillmentMessages": [
                {
                    "text": {
                        "text": [
                            "opps didnt match any product id"
                        ]
                    }
                }
            ]
        }
        return JSONResponse(content=fulfillment_message)


async def change_order(intent_name, parameters, cursor):
    global context_id
    try:
        order_id = parameters.get('number')
        logger.info(f"Received Intent: {intent_name}")
        logger.info(f"Received Parameters: {parameters}")
        logger.info(f"Order ID: {order_id}")

        if not order_id:
            raise ValueError("Order ID not provided")
        context_id = order_id
        order_details = get_order_details(int(order_id), cursor)
        logger.info(order_details)
        if not order_details:
            raise ValueError(f"Order with ID {order_id} not found")
        context_id = order_id

        # Construct the fulfillment message with product information
        elements = [
            {
                "title": f"Order ID {int(order_id)}",
                # Index 4 corresponds to the status in the tuple
                "subtitle": f"{order_details[0][3]}",
            }
        ]

        for product in order_details:
            elements.append({
                "title": f"{product[1]} - Quantity: {product[-3]}",  # Adjust indices based on your tuple structure
                "subtitle": f" Size: {product[-2]}, Colors: {product[-1]}",
            })

        for product in order_details:
            logger.info(product[2])
            prod = product[1]
            phone_no = product[2]

        fulfillment_message = {
            "fulfillmentMessages": [
                {
                    "payload": {
                        "facebook": {
                            "attachment": {
                                "type": "template",
                                "payload": {
                                    "template_type": "generic",
                                    "elements": elements
                                }
                            }
                        }
                    },
                    "platform": "FACEBOOK"
                },
                {
                    "quickReplies": {
                        "title": "What would you like to do ?",
                        "quickReplies": [
                            f"Delete Order: {int(order_id)}",
                            f"Change Ph:{phone_no}"
                        ]
                    },
                    "platform": "FACEBOOK"
                }
            ]
        }

        # Log the response
        logger.info(f"Generated Response: {fulfillment_message}")

        # Return fulfillment message
        return JSONResponse(content=fulfillment_message)

    except ValueError as ve:
        # Handle specific data errors
        logger.error(f"ValueError in handle_order_track: {str(ve)}", exc_info=True)
        raise HTTPException(status_code=404, detail=f"Order not found: {str(ve)}")

    except sqlite3.Error as e:
        # Handle database-related errors
        logger.error(f"Error in handle_order_track: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail="Internal Server Error")

    except Exception as e:
        # Log the unexpected exception
        logger.error(f"Unexpected error in handle_order_track: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail="Internal Server Error")


async def delete_order(intent_name, parameters, cursor):
    order_id = parameters.get('number')
    logger.info(order_id)

    try:
        # Fetch the order status
        cursor.execute("SELECT orderStatus FROM testorder WHERE OrderNumber = %s", (int(order_id),))
        status_result = cursor.fetchone()

        # Check if status_result is not None
        if status_result:
            status = status_result[0]  # Extract the status from the result tuple

            # Check if the order status is 'Initiated'
            if status == 'Initiated':
                # Delete the order from the database
                cursor.execute("DELETE FROM testorder WHERE OrderNumber = %s", (int(order_id),))

                # Prepare the fulfillment message
                fulfillment_message = {
                    "fulfillmentMessages": [
                        {
                            "text": {
                                "text": [f"Order {order_id} and its associated products have been deleted."]
                            }
                        }
                    ]
                }
            else:
                # If the order status is not 'Initiated'
                fulfillment_message = {
                    "fulfillmentMessages": [
                        {
                            "text": {
                                "text": [f"Order {order_id} cannot be deleted because its status is {status}."]
                            }
                        }
                    ]
                }
        else:
            # If the order with the specified ID does not exist
            fulfillment_message = {
                "fulfillmentMessages": [
                    {
                        "text": {
                            "text": [f"Order {order_id} does not exist."]
                        }
                    }
                ]
            }

        # Return the JSON response
        return JSONResponse(content=fulfillment_message)

    except Exception as e:
        # Log the exception or handle it as needed
        logger.error(f"Error deleting order: {str(e)}")
        return JSONResponse(content={"error": str(e)})


async def change_phone(intent_name, parameters, cursor):
    global context_id
    logger.info(context_id)
    logger.info(parameters)
    try:
        new_phone_number = parameters.get('phone-number')  # Assuming 'phone_number' is the parameter name

        # Fetch the order status
        cursor.execute("SELECT orderStatus, Phone FROM testorder WHERE OrderNumber = %s", (int(context_id),))
        order_result = cursor.fetchone()

        # Check if order_result is not None
        if order_result:
            status = order_result[0]  # Extract the status from the result tuple
            old_phone_number = order_result[1]  # Extract the old phone number

            # Check if the order status is 'Initiated'
            if status == 'Initiated':
                # Update the phone number in the database
                cursor.execute("UPDATE testorder SET Phone = %s WHERE OrderNumber = %s",
                               (new_phone_number, int(context_id)))

                # Prepare the fulfillment message
                fulfillment_message = {
                    "fulfillmentMessages": [
                        {
                            "text": {
                                "text": [
                                    f"Phone number updated for order {context_id}. Old number: {old_phone_number}, New number: {new_phone_number}"]
                            }
                        }
                    ]
                }
            else:
                # If the order status is not 'Initiated'
                fulfillment_message = {
                    "fulfillmentMessages": [
                        {
                            "text": {
                                "text": [
                                    f"Phone number for order {context_id} cannot be updated because its status is {status}."]
                            }
                        }
                    ]
                }
        else:
            # If the order with the specified ID does not exist
            fulfillment_message = {
                "fulfillmentMessages": [
                    {
                        "text": {
                            "text": [f"Order {context_id} does not exist."]
                        }
                    }
                ]
            }

        # Return the JSON response
        return JSONResponse(content=fulfillment_message)

    except Exception as e:
        # Log the exception or handle it as needed
        logger.error(f"Error updating phone number: {str(e)}")
        return JSONResponse(content={"error": str(e)})


@app.post("/")
async def handle_request(request: Request, payload: Payload):
    '''
    :param request: request obj (not used)
    :param payload: payload
    :return: runs function based on intent
    '''

    global cnx
    try:
        cnx, cursor = connect_to_db()
        # Extract the necessary information from the payload
        intent_name = payload.queryResult.intent.get("displayName")
        parameters = payload.queryResult.parameters

        # Logging
        logger.info(f"Received Intent: {intent_name}")
        logger.info(f"Received Parameters: {parameters}")

        # Handle specific intent
        if intent_name == "order.track - order_id":
            return await handle_order_track(intent_name, parameters, cursor)
        elif intent_name == "product.retrive_info":
            return await handle_retrieve_prod_info(intent_name, parameters, cursor)
        elif intent_name == "order.conformation":
            return await handle_order_query_retrieve_prod_info_yes(intent_name, parameters)
        elif intent_name == "order.retrive_cust_info":
            return await handle_retrieve_cust_info(intent_name, parameters, cursor)
        elif intent_name == "order.add_item":
            return await add_items(intent_name, parameters)
        elif intent_name == "order.change - custom":
            return await change_order(intent_name, parameters, cursor)
        elif intent_name == "order.change - custom - delete":
            return await delete_order(intent_name, parameters, cursor)
        elif intent_name == "order.change - custom - change phone - custom":
            return await change_phone(intent_name, parameters, cursor)
        else:
            return JSONResponse(content={"fulfillmentText": "Unhandled intent"})

    except KeyError as e:
        # Handle missing keys in the payload
        raise HTTPException(status_code=400, detail=f"Missing key in payload: {str(e)}")

    except (ValueError, TypeError) as e:
        # Handle specific data parsing errors
        raise HTTPException(status_code=400, detail=f"Invalid data in payload: {str(e)}")

    except Exception as e:
        # Log the unexpected exception
        logger.error(f"Unexpected error: {str(e)}", exc_info=True)
        raise HTTPException(status_code=500, detail="Internal Server Error")

    finally:
        # Close the database connection in the 'finally' block to ensure it happens regardless of success or failure
        if cnx:
            cnx.commit()
            cnx.close()