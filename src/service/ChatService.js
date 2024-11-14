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
        return axiosInstance.post(`/conversations/${conversationId}/messages`, {content});
    },
    saveResponse(conversationId, messageId, botId, content){
        return axiosInstance.post(`/conversations/${conversationId}/saveResponse`, {
            messageId,
            senderType: 'BOT',
            botId,
            content
        });
    },
    submitFeedback(feedbackDialogInfo){
        return axiosInstance.post(`/conversations/feedback`, feedbackDialogInfo);
    },
    submitRating(rateDialogInfo){
        return axiosInstance.post(`/bots/ratings`, rateDialogInfo);
    }
}
export default ChatService