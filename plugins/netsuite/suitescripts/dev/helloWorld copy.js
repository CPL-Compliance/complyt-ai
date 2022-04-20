/**
 *@NApiVersion 2.0
 *@NScriptType ClientScript
 */
define(['N/ui/dialog', 'N/record', 'N/https'], function (dialog, currentRecord, https) {
  function scanCustomer(context) {
    
    var thisRecord = context.currentRecord;
    var memoRef = thisRecord.getValue({
      fieldId: 'memo'
    });
    var cutomerRef = thisRecord.getValue({
      fieldId: 'entity'
    });
    var subsidiaryRef = thisRecord.getValue({
      fieldId: 'subsidiary'
    });
    var accountRef = thisRecord.getValue({
      fieldId: 'account'
    });
    var dateRef = thisRecord.getValue({
      fieldId: 'trandate'
    });
    var header={'Content-Type':'application/json'};
    body = {
      memoRef:memoRef,
      customerRef:cutomerRef,
      subsidiaryRef:subsidiaryRef,
      accountRef:accountRef,
      dateRef:dateRef
    }
      var apiURL='https://eoca3p3t6av82h.m.pipedream.net';
      try
      {
        var response=https.post({
          url:apiURL,
          headers:header,
          body:body
        });
      }
      catch(er02){
        log.error('ERROR',JSON.stringify(er02));
      }

  return {
    pageInit: scanCustomer
  };
}
})





//  define(['N/ui/dialog'],
//  function(dialog) {

//     // In Step 5, you put additional code here.
//     function helloWorld() {

//         // In steps 6-10, you put additional code here.
//         var options = {
//             title: 'Hello!',
//             message: 'Hello, World!'
//         };
//         try {

//             // In steps 8 and 9, you put additional code here.
//             dialog.alert(options);
//             log.debug ({
//                 title: 'Success',
//                 details: 'Alert displayed successfully'
//             });

//         } catch (e) {

//             // In Step 10, you put additional code here.
//             log.error ({
//                 title: e.name,
//                 details: e.message
//             });

//         }

//     }
//     return {
//         pageInit: helloWorld
//     };
// }
//  // In Step 4, you put additional code here.

// );





