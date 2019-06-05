package from_ecom.browser;

public class JavaScriptFactory {

    private JavaScriptFactory() {}

    public static String disableJQueryAnimations() {
        return "if (typeof $ != 'undefined') {$.fx.off = true;};";
    }

    public static String injectAngularStateServiceIn(String appName) {
        return "isAngularDone = false;\n" +
                "angular.module('"+appName+"', []).factory('AngularStateService', ['$timeout', function ($timeout) {\n" +
                "    return new function () {\n" +
                "        this.updateStateWhenAngularIsDone = function () {\n" +
                            "\t\tisAngularDone = false;\n" +
                            "\t\t$timeout(function(){\n" +
                                "\t\t\tisAngularDone = true;\n" +
                "            }, 0, false);\n" +
                "        };\n" +
                "    };\n" +
                "}]);";
    }

    public static String updateStateWhenAngularIsDone(String appName) {
        return "angular.injector(['ng', '"+appName+"']).get(\"AngularStateService\").updateStateWhenAngularIsDone();";
    }
}
