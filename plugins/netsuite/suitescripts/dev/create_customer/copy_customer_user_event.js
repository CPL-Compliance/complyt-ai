/**
*@NApiVersion 2.0
*@NScriptType UserEventScript
*/
define(['N/https'], function (https) {

    function afterSubmit(context) {
        
        var customerRecord = context.newRecord;
        
        var externalId = customerRecord.id;
        
        var name = customerRecord.getValue({
            fieldId: 'companyname'
        });

        var addressObj = customerRecord.getSublistSubrecord({
            sublistId: 'addressbook',
            fieldId: 'addressbookaddress',
            line: 0
        });
        
        var city = addressObj.getValue({
            fieldId: 'city'
        });
        
        var state = addressObj.getValue({
            fieldId: 'dropdownstate'
        });

        var country = addressObj.getValue({
            fieldId: 'country'
        });

        var street = addressObj.getValue({
            fieldId: 'addr1'
        });

        var zip = addressObj.getValue({
            fieldId: 'zip'
        });

        var address = {
            city:city,
            country:country,
            state:state,
            street:street,
            zip:zip
        };
        createCustomer(externalId, name, address);   
    }

    function createCustomer(externalId, name, address){

        var header={'Content-Type':'application/json', 'Accept':'application/json'};
        var url='https://complyt-test.herokuapp.com/v1/customer';
        var body = {
            externalId:externalId,
            name:name, 
            address:address
        };
        try
        {
            var res = https.put({
                url:url,
                headers:header,
                body:JSON.stringify(body)
            });
            log.debug(res.body);
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