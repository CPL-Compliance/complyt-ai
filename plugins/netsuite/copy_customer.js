/**
*@NApiVersion 2.1
*@NScriptType UserEventScript
*/
define(['N/https', 'N/encode'], function (https, encode) {

    const afterSubmit = context => {
        const customerRecord = context.newRecord;
        const externalId = customerRecord.id;
        const name = customerRecord.getValue({
            fieldId: 'companyname'
        });

        const addressObj = customerRecord.getSublistSubrecord({
            sublistId: 'addressbook',
            fieldId: 'addressbookaddress',
            line: 0
        });
        
        const city = addressObj.getValue({
            fieldId: 'city'
        });
        
        const state = addressObj.getValue({
            fieldId: 'dropdownstate'
        });

        const country = addressObj.getValue({
            fieldId: 'country'
        });

        const street = addressObj.getValue({
            fieldId: 'addr1'
        });

        const zip = addressObj.getValue({
            fieldId: 'zip'
        });

        const address = {
            city:city,
            country:country,
            state:state,
            street:street,
            zip:zip
        };

        createCustomer(externalId, name, address);   
    }

    const createCustomer = (externalId, name, address) => {
        const stringInput = "admin:admin";
		const base64EncodedString = encode.convert({
			string: stringInput,
			inputEncoding: encode.Encoding.UTF_8,
			outputEncoding: encode.Encoding.BASE_64
		});

		const authHeader = 'Basic ' + base64EncodedString;
        const header = {
            'Content-Type':'application/json',
             'Accept':'application/json',
             'Authorization' : authHeader
        };
        
        const url='https://complyt-test.herokuapp.com/v1/customers';
        const body = {
            externalId:externalId,
            name:name, 
            address:address
        };
        try
        {
            const res = https.put({
                url:url,
                headers:header,
                body:JSON.stringify(body)
            });
        }
        catch(e)
        {
          log.error('ERROR',JSON.stringify(e));
        }

    }

    return {
        afterSubmit: afterSubmit
    }
});