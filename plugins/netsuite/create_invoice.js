/**
* @NApiVersion 2.1
* @NScriptType UserEventScript
* @NAmdConfig /SuiteScripts/Config/config.json
*/
define(['N/record', '/SuiteScripts/Utils/httpUtil.js', 'invoiceConfiguration'], (record, httpUtil, invoiceConfiguration) => {

    const afterSubmit = context => {
        
        const invoiceRecord = context.newRecord;
        const invoiceExternalId = invoiceRecord.id;
        
        const rec = record.load({ type: record.Type.INVOICE, id: invoiceExternalId, isDynamic: true });
        
        removeSalesTaxFromItems(rec);
        const itemsLength = rec.getLineCount({ sublistId : 'item' });
        const invoice = createInvoice(invoiceRecord, invoiceExternalId, itemsLength, rec);
        const invoiceWithSalesTax = setSalesTax(invoice);
        const salesTaxAmount = invoiceWithSalesTax.salesTax.amount;
        const salesTaxRate = invoiceWithSalesTax.salesTax.salesTaxRate.taxRate;
        insertSalesTaxAsItem(rec, itemsLength, salesTaxAmount,salesTaxRate);
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

    const createInvoice = (invoiceRecord, invoiceExternalId, itemsLength, rec) => {
        const customerExternalId = invoiceRecord.getValue({ fieldId: 'entity' });
        log.debug('Looking for customer with external id',customerExternalId);
        const customerId = getCustomerId(customerExternalId);

        const billingAddrRecord = invoiceRecord.getSubrecord({ fieldId: 'billingaddress' });
        const shippingAddrRecord = invoiceRecord.getSubrecord({ fieldId: 'shippingaddress' });
        const billingAddress = getAddress(billingAddrRecord);
        const shippingAddress = getAddress(shippingAddrRecord);

        const items = getItems(rec, itemsLength);
        const invoice = sendInvoice(invoiceExternalId, customerId, billingAddress, shippingAddress, items);
        log.audit('Invoice created');
        log.debug('Invoice details',invoice);

        return invoice;
    }

    const sendInvoice = (invoiceExternalId, customerId, billingAddress, shippingAddress, items) => {

        const url = invoiceConfiguration.ORDER_URL + '/' + invoiceExternalId;
        const body = JSON.stringify
        ({
            externalId: invoiceExternalId,
            customerId: customerId,
            billingAddress: billingAddress,
            shippingAddress: shippingAddress,
            items: items,
            orderStatus: invoiceConfiguration.ACTIVE_STATUS
        });

        const errorMessage = 'Could not create invoice';
        const invoice = httpUtil.sendPutRequest(url, body, errorMessage);

        return invoice;

    }

    const setSalesTax = invoice => {

        const url = invoiceConfiguration.ORDER_URL + '/' + invoice.externalId + '/salesTax';
        const body = {};
        const errorMessage = 'Could not update invoice with sales tax';

        log.audit('Calculating sales tax for invoice');

        const invoiceWithSalesTax = httpUtil.sendPutRequest(url, body, errorMessage);
        if(!invoiceWithSalesTax.salesTax)
        {
            throw new Error('Could not get sales tax for this transaction. Please check the inserted shipping address')
        }
        log.debug('Sales tax details',invoiceWithSalesTax.salesTax);
        return invoiceWithSalesTax;
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

            const amount = rec.getSublistValue({
                sublistId : 'item',
                fieldId : 'amount',
                line : i
            });

            const quantity = rec.getSublistValue({
                sublistId : 'item',
                fieldId : 'quantity',
                line : i
            });

            const rate = amount / quantity;

            const description = rec.getSublistValue({
                sublistId : 'item',
                fieldId : 'description',
                line : i
            });

            items[i] = {
                unitPrice: rate,
                totalPrice: amount,
                name: name,
                quantity: quantity,
                description: description,
                taxCode:""
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

    const getCustomerId = (customerId) => {
        try
        {
            const url = invoiceConfiguration.CUSTOMER_URL + '/' + customerId;

            const errorMessage = 'No customer with id ' + customerId + ' was found';

            const customer = httpUtil.sendGetRequest(url, errorMessage);
            return customer.id;
        }
        catch(err)
        {
            throw new Error('Customer does not exist in the system');
        }
    }

    const insertSalesTaxAsItem = (rec, itemsLength, salesTaxAmount, salesTaxRate) => {
        const salesTaxRateAsPercentage = (parseFloat(salesTaxRate) * 100).toFixed(3);
        const rateDescription = "Sales Tax rate : " + salesTaxRateAsPercentage + "%";
        log.audit('Inserting Sales tax to items sublist');
        rec.insertLine({ sublistId: "item", line: itemsLength });

        rec.setCurrentSublistText({ sublistId: "item", fieldId: "item", text: "Sales Tax" });
        rec.setCurrentSublistValue({sublistId: "item", fieldId: "quantity", value: 1 });
        rec.setCurrentSublistValue({ sublistId: "item", fieldId: "amount", value: salesTaxAmount });
        rec.setCurrentSublistValue({ sublistId: "item", fieldId: "description", value: rateDescription });

        rec.commitLine({ sublistId: 'item' });
        rec.save();
    }

    return {
        afterSubmit: afterSubmit
    }
});