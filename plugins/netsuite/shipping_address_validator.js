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
            const invalidFields = [];
            const streetValid = street != undefined && street.trim() !== '';
            if(!streetValid){
                invalidFields.push('street');
            }

            const cityValid = city && city.trim() !== '';
            if(!cityValid){
                invalidFields.push('city');
            }

            const stateValid = state && state.trim() !== '';
            if(!stateValid){
                invalidFields.push('state');
            }

            const zipValid = zip && zip.trim() !== '';
            if(!zipValid){
                invalidFields.push('zip');
            }
            
            const countryValid = country && country.trim() !== '';
            if(!countryValid){
                invalidFields.push('country');
            }
            
            if(invalidFields.length !== 0){
                throw new Error('Please add ' + invalidFields.toString());
            }
        }

        return {
            saveRecord: saveRecord
        }
});