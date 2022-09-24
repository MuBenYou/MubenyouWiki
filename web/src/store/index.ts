import { createStore } from 'vuex'

// export default createStore({
//   state: {
//   },
//   mutations: {
//   },
//   actions: {
//   },
//   modules: {
//   }
// })

const store = createStore({
  state: {
    user:{}
  },
  mutations: {
    setUser(state, user) {
      state.user = user;
    }
  },
  actions: {
  },
  modules: {
  }
})

export default store;