import { createRouter, createWebHistory } from 'vue-router'
import PkIndexView from '@/views/pk/PkIndexView'
import RanklistIndexView from '@/views/ranklist/RanklistIndexView'
import RecordIndexView from '@/views/record/RecordIndexView'
import UserBotIndexView from '@/views/user/bot/UserBotIndexView'
import UserAccountLoginView from '@/views/user/account/UserAccountLoginView'
import UserAccountRegisterView from '@/views/user/account/UserAccountRegisterView'
import NotFound from '@/views/error/NotFound'
import store from '@/store/index'

//Vue routes views according to path (defined here)
const routes = [
  {
    path: "/",
    name: "home",
    //same to navigate in react
    redirect: "/pk/",
    meta: {
      request_auth: true,
    },
  },
  {
    path: "/pk/",
    name: "pk_index",
    component: PkIndexView,
    meta: {
      request_auth: true,
    },
  },
  {
    path: "/record/",
    name: "record_index",
    component: RecordIndexView,
    meta: {
      request_auth: true,
    },
  },
  {
    path: "/ranklist/",
    name: "ranklist_index",
    component: RanklistIndexView,
    meta: {
      request_auth: true,
    },
  },
  {
    path: "/user/bot/",
    name: "user_bot_index",
    component: UserBotIndexView,
    meta: {
      request_auth: true,
    },
  },
  {
    path: "/user/account/login/",
    name: "user_account_login",
    component: UserAccountLoginView,
    meta: {
      request_auth: false,
    },
  },
  {
    path: "/user/account/register/",
    name: "user_account_register",
    component: UserAccountRegisterView,
    meta: {
      request_auth: false,
    },
  },
  {
    path: "/404/",
    name: "404",
    component: NotFound,
    meta: {
      request_auth: false,
    },
  },
  {
    //match any path
    path: "/:catchAll(.*)",
    redirect: "/404/",
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

//frontent authorization, if not login, router will change the page to login page
//beforeEach will exexcute the function before routing
//to: the page routing to, which is an object in routes
//from: the page routing from, which is an object in routes
//next: if the next operation would be done
router.beforeEach(
  (to, from, next) => {
    if(to.meta.request_auth && !store.state.user.is_login){
      next({name: "user_account_login"});
    }else{
      next();
    }
  }
);

export default router
