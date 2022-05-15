 /**
    *@NApiVersion 2.1
    *@NScriptType ClientScript
*/
define(['N/ui/dialog'], function (dialog) {

        function saveRecord(context) {
            var currentRecord = context.currentRecord;
            const shippingAddrRecord = currentRecord.getSubrecord({ fieldId: 'shippingaddress' });
            
            const street = shippingAddrRecord.getValue({
            fieldId: 'addr1'
            });

            const city = shippingAddrRecord.getValue({
            fieldId: 'city'
            });

            const state = shippingAddrRecord.getValue({
            fieldId: 'state'
            });

            const zip = shippingAddrRecord.getValue({
                fieldId: 'zip'
            });

            const country = shippingAddrRecord.getValue({
                fieldId: 'country'
            });
            
            try 
            {
                validateAddress(street,city,state,zip,country);
                return true;
            }
            catch(error)
            {
                dialog.alert(
                    {
                        title: 'Invalid address',
                        message: error.message 
                    }
                );
                return false;
            }
                        
        }

        const validateAddress = (street, city, state, zip, country) => {
            const streetValid = street != null && street.trim() !== '';
            if(!streetValid) {
                throw new Error('Please add street for the shipping address');
            }

            const cityValid = city != null && city.trim() !== '';
            if(!cityValid) {
                throw new Error('Please add city for the shipping address');
            }

            const stateValid = state != null && state.trim() !== '';
            if(!stateValid) {
                throw new Error('Please add state for the shipping address');
            }

            const zipValid = zip != null && zip.trim() !== '';
            if(!zipValid) {
                throw new Error('Please add zip for the shipping address');
            }
            
            const countryValid = country != null && country.trim() !== '';
            if(!countryValid) {
                throw new Error('Please add country for the shipping address');
            }
        }

        return {
            saveRecord: saveRecord
        }
});