import { createStore } from 'vuex';

const store = createStore({
    state() {
        return {
            personalProfile: {}
        };
    },
    mutations: {
        setPersonalProfile(state, profile) {
            state.personalProfile = profile;
        }
    },
    actions: {
        updatePersonalProfile({ commit }, profile) {
            commit('setPersonalProfile', profile);
        }
    },
    getters: {
        personalProfile: (state) => state.personalProfile
    }
});

export default store;