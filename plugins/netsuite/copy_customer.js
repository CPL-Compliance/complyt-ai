/**
* @NApiVersion 2.1
* @NScriptType UserEventScript
* @NAmdConfig /SuiteScripts/Libraries/config.json
*/
define(['/SuiteScripts/Utils/httpUtil.js', 'customerConfiguration'], (httpUtil, customerConfiguration) => {

    const afterSubmit = context => {
        try {
            const customerRecord = context.newRecord;
            const externalId = customerRecord.id;
            const name = customerRecord.getValue({ fieldId: 'companyname' });
            const address = getAddress(customerRecord);

            const customer = createCustomer(externalId, name, address); 
            log.debug('successfully created customer', customer);  
        }
        catch(err){
            log.debug('Could not create new customer', err);
        }
    }

    const getAddress = customerRecord => {
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
        return address;
    }

    const createCustomer = (externalId, name, address) => {
        
        const url = customerConfiguration.BASE_URL;
        const body = JSON.stringify({
            externalId:externalId,
            name:name, 
            address:address
        });
        const errorMessage = 'Could not create customer with Id ' + externalId;
        const customer = httpUtil.sendPutRequest(url, body, errorMessage);
        return customer;
    }

    return {
        afterSubmit: afterSubmit
    }
});