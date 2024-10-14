import { createStore } from 'vuex';

const store = createStore({
    state() {
        return {
            personalProfile: {
                image: 'https://cdn.pixabay.com/photo/2015/10/05/22/37/blank-profile-picture-973460_960_720.png',
                imageName: 'default profile.png',
                name: 'tome',
                email: 'hello@gmail.com',
                bio: 'Hello, I am a new user',
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