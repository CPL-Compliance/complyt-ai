package test_utils.integration_tests;

public class TestUtilities {
    public static String apiKeyClientId = "78fd4034-53af-4144-b2da-27ac31cdf45c";
    public static String apiKeyClientSecret = "3d446591-d839-4906-97fe-85e1b51df0c8";
    public static String apiKeyJsonExample() {
        return "{\n" +
                "    \"clientId\":\"" + apiKeyClientId + "\",\n" +
                "    \"clientSecret\":\"" + apiKeyClientSecret + "\"\n" +
                "}";
    }

}
