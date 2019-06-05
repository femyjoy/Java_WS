package from_ecom.browser.webclient

class ProductCatalogueClientHeadersFactory {
    static def Map<String, String> agentAcquisition() {
        return ['X-TouchPoint': 'agent', 'X-PartnerId': 'o2_in', 'X-UserId': 'karunukar', 'X-IdType': 'WebSealAccessManagerUserId', "X-Channel" : "VoiceNew", "Content-Type" : "application/json"]
    }

    static def Map<String, String> customerAcquisition(String userId) {
        return ['X-TouchPoint': 'cfa', 'X-PartnerId': 'o2', 'X-UserId': userId, 'X-IdType': 'someKindOfId', "X-Channel" : "ConsumerNew", "Content-Type" : "application/json"]
    }

    static def Map<String, String> customerUpgrade(String userId) {
        return ['X-TouchPoint': 'cfu', 'X-PartnerId': 'o2', 'X-UserId': userId, 'X-IdType': 'someKindOfId', "X-Channel" : "ConsumerUpgrade", "Content-Type" : "application/json"]
    }

    static def Map<String, String> agentUpgrade() {
        return ['X-TouchPoint': 'agent', 'X-PartnerId': 'o2_in', 'X-UserId': 'karunukar', 'X-IdType': 'WebSealAccessManagerUserId', "X-Channel" : "VoiceUpgrade", "Content-Type" : "application/json"]
    }
}