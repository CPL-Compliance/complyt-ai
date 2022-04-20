/**
 *@NApiVersion 2.0
 *@NScriptType ClientScript
 */

 define(['N/ui/dialog'],
 function(dialog) {

    // In Step 5, you put additional code here.
    function helloWorld() {

        // In steps 6-10, you put additional code here.
        var options = {
            title: 'Hello!',
            message: 'Hello, World!'
        };
        try {

            // In steps 8 and 9, you put additional code here.
            dialog.alert(options);
            log.debug ({
                title: 'Success',
                details: 'Alert displayed successfully'
            });

        } catch (e) {

            // In Step 10, you put additional code here.
            log.error ({
                title: e.name,
                details: e.message
            });

        }

    }
    return {
        pageInit: helloWorld
    };
}
 // In Step 4, you put additional code here.

);





