// src/service/ProfileService.js
import axiosInstance from "@/service/axiosInstance";

const ProfileService = {
    getProfile(id) {
        if (!id) {
            return axiosInstance.get('/profile');
        } else {
            return axiosInstance.get(`/profile/${id}`);
        }
    },
    deleteRobot(botId) {
        return axiosInstance.delete(`/bots/${botId}`);
    },
    changeAvatar(formData) {
        return axiosInstance.put('/profile/change-avatar', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });
    },
    changeBio(newBio) {
        return axiosInstance.put('/profile/change-bio', { newBio }, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    },
    getUserStatistics() {
        return axiosInstance.get('/profile/statistics');
    },
    postFeedback(feedbackForm) {
        return axiosInstance.post('/users/feedback', feedbackForm, {
            headers: {
                'Content-Type': 'application/json'
            }
        });
    }
};

export default ProfileService;