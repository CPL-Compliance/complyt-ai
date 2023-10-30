import csv
import json
import random
import requests
import time  # Import the time module

# Define the CSV file path
csv_file_path = 'filtered_file.csv'  # Replace with your actual CSV file path

# List to store extracted values for each row
addresses = []

# Read the CSV file and extract specified column values for each row
with open(csv_file_path, 'r', newline='', encoding='mac_roman') as csvfile:
    reader = csv.DictReader(csvfile, delimiter='\t')

    for row in reader:
        items_list = list(row.items())

        values = items_list[0][1].split(',')

        zip = str(values[0]).zfill(5).replace('"', '')
        city = values[3].replace('"', '')
        state = values[4].replace('"', '')
        county = values[11].replace('"', '')

        addresses.append({'zip': zip, 'state': state, 'city': city,
                          'county': county, 'country': 'US', 'isPartial': True})

random.shuffle(addresses)

for address in addresses:

    url = ''  # Replace with your actual URL
    query_params = address
    bearer_token = ''  # Replace with your actual Bearer token

    headers = {
        'Content-Type': 'application/json',
        'Authorization': f'Bearer {bearer_token}'
    }

    try:
        response = requests.get(url, headers=headers, params=query_params)
        response.raise_for_status()

        original_zip_code = address['zip']
        original_state = address['state']
        original_city = address['city']
        original_county = address['county']

        complyt_sales_tax_rates_from_eyal_dev = json.loads(
            response.content.decode('utf-8'))

        zip_code_from_api = complyt_sales_tax_rates_from_eyal_dev["address"]['zip']
        city_from_api = complyt_sales_tax_rates_from_eyal_dev["address"]['city']
        state_from_api = complyt_sales_tax_rates_from_eyal_dev["address"]['state']
        county_from_api = complyt_sales_tax_rates_from_eyal_dev["address"]['county']

        if (original_zip_code is not None and zip_code_from_api is not None and str(original_zip_code) != str(
                zip_code_from_api)) or (
                original_city is not None and city_from_api is not None and original_city.upper() != city_from_api.upper()) or (
                original_state is not None and state_from_api is not None and original_state.upper() != state_from_api.upper()) or (
                original_county is not None and county_from_api is not None and original_county.upper() != county_from_api.upper()):
            with open("city_county_state_output_file.txt", "a") as outputfile:
                outputfile.write(
                    f"Original address- Zip: {original_zip_code}, City: {original_city}, State: {original_state}, County: {original_county} \n" +
                    f"Address from API- Zip: {zip_code_from_api}, City: {city_from_api}, State: {state_from_api}, County: {county_from_api} \n\n")

        time.sleep(1)  # Add a delay between each iteration

    except requests.exceptions.HTTPError as errh:
        with open("failed_query_paramas", "a") as failed_query_files:
            failed_query_files.write(f"HTTP Error: {str(address)} \n")

    except requests.exceptions.ConnectionError as errc:
        with open("failed_query_paramas", "a") as failed_query_files:
            failed_query_files.write(f"Error Connecting: {str(address)} \n")

    except requests.exceptions.Timeout as errt:
        with open("failed_query_paramas", "a") as failed_query_files:
            failed_query_files.write(f"Timeout Error: {str(address)} \n")

    except requests.exceptions.RequestException as err:
        with open("failed_query_paramas", "a") as failed_query_files:
            failed_query_files.write(
                f"Something went wrong: {str(address)} \n")
