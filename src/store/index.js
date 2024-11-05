import { createStore } from 'vuex';

const store = createStore({
    state() {
        return {
            personalProfile: {
                username: 'username',
                email: 'hello@gmail',
                role: 'USER',
                credits: 0,
                avatarUrl: 'https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png',
                bio: 'Hello, I am a new user',
                token: 0,
            },
            isLoggedIn: false
        };
    },
    mutations: {
        setPersonalProfile(state, profile) {
            state.personalProfile = profile;
        },
        setLoginState(state, isLoggedIn) {
            state.isLoggedIn = isLoggedIn;
        }
    },
    actions: {
        updatePersonalProfile({ commit }, profile) {
            commit('setPersonalProfile', profile);
        },
        updateLoginState({ commit }, isLoggedIn) {
            commit('setLoginState', isLoggedIn);
            localStorage.setItem('isLoggedIn', isLoggedIn);
        }
    },
    getters: {
        personalProfile: (state) => state.personalProfile,
        isLoggedIn: (state) => state.isLoggedIn
    }
});

export default store;