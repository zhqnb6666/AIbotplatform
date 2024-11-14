// src/service/ProfileService.js
import axiosInstance from "@/service/axiosInstance";

const ProfileService = {
    getProfile() {
        return axiosInstance.get('/profile');
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
    }
};

export default ProfileService;