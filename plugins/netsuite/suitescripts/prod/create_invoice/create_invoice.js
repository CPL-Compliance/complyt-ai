/**
*@NApiVersion 2.1
*@NScriptType UserEventScript
*/
define(['N/record', 'N/https', 'N/encode'], (record, https, encode) => {

    const afterSubmit = context => {
        const invoiceRecord = context.newRecord;
        const invoiceExternalId = invoiceRecord.id;
        
        const rec = record.load({
            type: record.Type.INVOICE,
            id: invoiceExternalId,
            isDynamic: true
        });
        
        removeSalesTaxFromItems(rec);
        const itemsLength = rec.getLineCount({ sublistId : 'item' });
        const authHeader = getAuthHeader(encode);
        
        const invoice = createInvoice(invoiceRecord, invoiceExternalId, itemsLength, rec, authHeader);
        
        const invoiceWithSalesTax = setSalesTax(invoice, authHeader);
        console.log(invoiceWithSalesTax);
        const salesTaxAmount = invoiceWithSalesTax.salesTax.amount;

        insertSalesTaxAsItem(rec, itemsLength, salesTaxAmount);
    }

    const setSalesTax = (invoice, authHeader) => {
        
        const header = {
            'Content-Type':'application/json',
             'Accept':'application/json',
             'Authorization' : authHeader
        };
        
        var url='https://complyt-test.herokuapp.com/v1/orders/' + invoice.externalId + '/salesTax';
        try
        {
            var res = https.put({
                url: url,
                headers: header,
            });
            response = JSON.parse(res.body);
            return response;
        }
        catch(e)
        {
            log.error('ERROR',JSON.stringify(e));
        }
    }

    const removeSalesTaxFromItems = rec => {

        const itemsLength = rec.getLineCount({ sublistId : 'item' });
        const salesTaxAsString = "Sales Tax";

        for (var i = itemsLength - 1 ; i >= 0 ; i--) 
        {
            const name = rec.getSublistText({ sublistId: "item", fieldId: "item",line:i });

            if(name === salesTaxAsString)
            {
                rec.removeLine({ sublistId: "item", line: i });
                log.debug('Line was removed ', name + " at line " + i);
            }
        }
    }

    const createInvoice = (invoiceRecord, invoiceExternalId, itemsLength, rec, authHeader) => {
        const customerExternalId = invoiceRecord.getValue({ fieldId: 'entity' });
        const customer = validateCustomer(customerExternalId, authHeader);
        const customerId = customer.id;
        
        const billingAddrRecord = invoiceRecord.getSubrecord({ fieldId: 'billingaddress' });
        const shippingAddrRecord = invoiceRecord.getSubrecord({ fieldId: 'shippingaddress' });
        const billingAddress = getAddress(billingAddrRecord);
        const shippingAddress = getAddress(shippingAddrRecord);

        const items = getItems(rec, itemsLength);
        const invoice = sendInvoice(invoiceExternalId, customerId, billingAddress, shippingAddress, items, authHeader);
        return invoice;
    }

    const sendInvoice = (invoiceExternalId, customerId, billingAddress, shippingAddress, items, authHeader) => {
        
        const header = {
            'Content-Type':'application/json',
             'Accept':'application/json',
             'Authorization' : authHeader
        };
        
        const url = 'https://complyt-test.herokuapp.com/v1/orders/';
        const body = 
        {
            externalId: invoiceExternalId,
            customerId: customerId,
            billingAddress: billingAddress,
            shippingAddress: shippingAddress,
            items: items,
            orderStatus:'ACTIVE'
        };
        
        try
        {
            const res = https.put({
                url: url,
                headers: header,
                body: JSON.stringify(body)
            });
            const response = JSON.parse(res.body);
            return response;
        }
        catch(e)
        {
            log.error('ERROR',JSON.stringify(e));
        }
    }

    const getItems = (rec, numItemLines) => {
        const items = [];

        for (let i = 0; i < numItemLines; i++) 
        {
            
                const name = rec.getSublistText({
                    sublistId: 'item',
                    fieldId: 'item',
                    line: i
                });
                
                const totalPrice = rec.getSublistValue({
                    sublistId : 'item',
                    fieldId : 'amount',
                    line : i
                });
                
                const quantity = rec.getSublistValue({
                    sublistId : 'item',
                    fieldId : 'quantity',
                    line : i
                });

                const unitPrice = totalPrice / quantity;

                const description = rec.getSublistValue({
                    sublistId : 'item',
                    fieldId : 'description',
                    line : i
                });

                items[i] = {
                    unitPrice: unitPrice,
                    quantity: quantity,
                    totalPrice: totalPrice,
                    description: description,
                    name: name,
                    taxCode:"TBD"
                }
            }
            
        return items;
    }

    const getAddress = addressRecord => {
        const city = addressRecord.getValue({
            fieldId: 'city'
        });
        
        const country = addressRecord.getValue({
            fieldId: 'country'
        });
        
        const state = addressRecord.getValue({
            fieldId: 'state'
        });
        
        const street = addressRecord.getValue({
            fieldId: 'addr1'
        });
        
        const zip = addressRecord.getValue({
            fieldId: 'zip'
        });

        const address = 
        {
            city:city,
            country:country,
            state:state,
            street:street,
            zip:zip
        };

        return address;
    }

    const validateCustomer = (customerId, authHeader) => {
        log.debug('in validate customer')
        

        const header = {
            'Content-Type':'application/json',
             'Accept':'application/json',
             'Authorization' : authHeader
        };
        const url = 'https://complyt-test.herokuapp.com/v1/customers/' + customerId;
        
        try
        {
            const res = https.get({
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

    const getAuthHeader = encode => {
        const stringInput = "admin:admin";
		const base64EncodedString = encode.convert({
			string: stringInput,
			inputEncoding: encode.Encoding.UTF_8,
			outputEncoding: encode.Encoding.BASE_64
		});

		const authHeader = 'Basic ' + base64EncodedString;
        return authHeader;
    }

    const insertSalesTaxAsItem = (rec, itemsLength, salesTaxAmount) => {
        rec.insertLine({ sublistId: "item", line: itemsLength });
        rec.setCurrentSublistText({ sublistId: "item", fieldId: "item", text: "Sales Tax" });
        rec.setCurrentSublistValue({sublistId: "item", fieldId: "quantity", value: 1 });
        rec.setCurrentSublistValue({ sublistId: "item", fieldId: "amount", value: salesTaxAmount });
        rec.commitLine({ sublistId: 'item' });
        rec.save();
    }

    return {
        afterSubmit: afterSubmit
    }
});
