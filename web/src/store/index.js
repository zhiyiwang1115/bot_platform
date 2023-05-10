import { createStore } from 'vuex'
import ModuleUser from './user'
import ModulePk from './pk'
import ModuleRecord from './record'

export default createStore({
  state: {
  },
  getters: {
  },
  mutations: {
  },
  actions: {
  },
  modules: {
    //modules: import sub data store
    user: ModuleUser,
    pk: ModulePk,
    record: ModuleRecord,
  }
})
