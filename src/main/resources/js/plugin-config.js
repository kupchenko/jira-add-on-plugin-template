AJS.toInit(function () {

    var serviceUrl = AJS.contextPath() + "/rest/plugin/1.0/plugin-config";

    function populateConfig() {
        AJS.$.ajax({
            url: serviceUrl,
            dataType: "json",
            success: function (config) {
                AJS.$("#someField").val(config.someField);
                AJS.$("#anotherField").val(config.anotherField);
            },
            error: function (response) {
                var object = JSON.parse(response.responseText);
                console.log('Error while updating plugin config: code: ' + object.code + ', message: ' + object.message);
                configResponseElem = AJS.$("#saveConfigResponse");
                configResponseElem.removeClass().addClass("aui-message aui-message-error");
                configResponseElem.text(object.message);
                configResponseElem.show();
                configResponseElem.delay(10000).fadeOut();
            }
        });
    }

    populateConfig();

    function updateConfig() {
        console.log("Updating config");
        var data = {
            someField: AJS.$("#someField").val(),
            anotherField: AJS.$("#anotherField").val()
        };

        AJS.$.ajax({
            url: serviceUrl,
            type: "PUT",
            contentType: "application/json",
            data: JSON.stringify(data),
            processData: false,
            success: function (response) {
                var msg = 'Configuration saved';
                console.log(msg);
                configResponseElem = AJS.$("#saveConfigResponse");
                configResponseElem.removeClass().addClass("aui-message aui-message-success");
                configResponseElem.text(msg);
                configResponseElem.show();
                configResponseElem.delay(10000).fadeOut();
            },
            error: function (response) {
                var object = JSON.parse(response.responseText);
                console.log('Error while updating plugin config: code: ' + object.code + ', message: ' + object.message);
                configResponseElem = AJS.$("#saveConfigResponse");
                configResponseElem.removeClass().addClass("aui-message aui-message-error");
                configResponseElem.text(object.message);
                configResponseElem.show();
            }
        });
    }

    AJS.$("#plugin-config-form").submit(function (e) {
        e.preventDefault();
        AJS.$("#saveConfigResponse").hide();
        updateConfig();
    });
});
