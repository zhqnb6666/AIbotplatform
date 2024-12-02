// src/service/chatService.js
import axiosInstance from "@/service/axiosInstance";

const ChatService = {
    getBotInfo(botId){
        return axiosInstance.get(`/bots/${botId}`);
    },
    createNewConversation(conversationBasicInfo){
        return axiosInstance.post(`/conversations`, conversationBasicInfo);
    },
    loadConversationHistory(conversationId){
        return axiosInstance.get(`/conversations/${conversationId}/chatHistory`);
    },
    sendMessage(conversationId, content){
        return axiosInstance.post(`/conversations/${conversationId}/messages`, content);
    },
    getGreeting(botId){
        return axiosInstance.get(`/bots/${botId}/greeting`);
    },
    saveResponse(conversationId, messageId, botId, content){
        return axiosInstance.post(`/conversations/${conversationId}/saveResponse`, {
            messageId: messageId,
            senderType: 'BOT',
            botId: botId,
            content: content
        });
    },
    submitFeedback(feedbackDialogInfo){
        return axiosInstance.post(`/conversations/feedback`, feedbackDialogInfo);
    },
    submitRating(rateDialogInfo){
        return axiosInstance.post(`/bots/ratings`, rateDialogInfo);
    },
    predictTitle(content, conversationId) {
        return axiosInstance.post(`/conversations/predict-title?conversationId=${conversationId}`, content);
    },
    predictNext(content) {
        return axiosInstance.post(`/conversations/predict-next`, content);
    },
    uploadFile(formData, botId) {
        return axiosInstance.post(`/bots/${botId}/rag`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
    },
}
export default ChatService