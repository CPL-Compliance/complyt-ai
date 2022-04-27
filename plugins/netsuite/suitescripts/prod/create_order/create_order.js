/**
*@NApiVersion 2.0
*@NScriptType UserEventScript
*/
define(['N/record', 'N/https'], function (record, https) {

    function afterSubmit(context) {
        
        var invoiceRecord = context.newRecord;
 
        var customerExternalId = invoiceRecord.getValue({ fieldId: 'entity' });
        var billingAddrRecord = invoiceRecord.getSubrecord({ fieldId: 'billingaddress' });
        var shippingAddrRecord = invoiceRecord.getSubrecord({ fieldId: 'shippingaddress' });
        
        var invoiceExternalId = invoiceRecord.id;

        var customer = validateCustomer(customerExternalId);
        var customerId = customer.id;

        var billingAddress = getAddress(billingAddrRecord);
        var shippingAddress = getAddress(shippingAddrRecord);
        var items = getItems(invoiceRecord);
        

        const invoice = createInvoice(invoiceExternalId, customerId, billingAddress, shippingAddress, items);
        const invoiceWithSalesTax = createInvoiceWithSalesTax(invoice);
    }

    function createInvoiceWithSalesTax(invoice){
        var header={'Content-Type':'application/json', 'Accept':'application/json'};
        var url='https://complyt-test.herokuapp.com/v1/orders/' + invoice.externalId + '/salesTax';
        var body = {};
        try
        {
            var res = https.put({
                url: url,
                headers: header,
                body: JSON.stringify(body)
            });
            return JSON.parse(res.body);
        }
        catch(e)
        {
            log.error('ERROR',JSON.stringify(e));
        }
    }

    function createInvoice(invoiceExternalId, customerId, billingAddress, shippingAddress, items){
        var header={'Content-Type':'application/json', 'Accept':'application/json'};
        var url='https://complyt-test.herokuapp.com/v1/orders/';
        var body = { 
            externalId: invoiceExternalId,
            customerId: customerId,
            billingAddress: billingAddress,
            shippingAddress: shippingAddress,
            items: items
        };
        
        try
        {
            var res = https.put({
                url: url,
                headers: header,
                body: JSON.stringify(body)
            });
            return JSON.parse(res.body);
        }
        catch(e)
        {
            log.error('ERROR',JSON.stringify(e));
        }
    }

    function getItems(invoiceRecord){
        var items = [];
        var numLines = invoiceRecord.getLineCount({
            sublistId : 'item'
        });
        for (var i = 0; i < numLines; i++) {
                var amount = invoiceRecord.getSublistValue({
                    sublistId : 'item',
                    fieldId : 'amount',
                    line : i
                });
                // var item = invoiceRecord.getSublistValue({
                //     sublistId: 'item',
                //     fieldId: 'itemtype',
                //     line: i
                // });
                // log.debug('item',item);
                var quantity = invoiceRecord.getSublistValue({
                    sublistId : 'item',
                    fieldId : 'quantity',
                    line : i
                });
                var description = invoiceRecord.getSublistValue({
                    sublistId : 'item',
                    fieldId : 'description',
                    line : i
                });

                items[i] = {
                    price: amount,
                    name: 'item',
                    quantity: quantity,
                    description: description,
                    taxCode:"1"
                }
            }

        return items;
    }

    function getAddress(addressRecord){
        var city = addressRecord.getValue({
            fieldId: 'city'
        });
        
        var country = addressRecord.getValue({
            fieldId: 'country'
        });
        
        var state = addressRecord.getValue({
            fieldId: 'state'
        });
        
        var street = addressRecord.getValue({
            fieldId: 'addr1'
        });
        
        var zip = addressRecord.getValue({
            fieldId: 'zip'
        });

        address = {
            city:city,
            country:country,
            state:state,
            street:street,
            zip:zip
        };

        return address;
    }

    function validateCustomer(customerId){
        var header={'Content-Type':'application/json', 'Accept':'application/json'};
        var url='https://complyt-test.herokuapp.com/v1/customers/findByExternalId?externalId=' + customerId;
        
        try
        {
            var res = https.get({
                url:url,
                headers:header
            });
            
            return JSON.parse(res.body);
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
