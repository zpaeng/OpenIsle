import { createRouter, createWebHistory } from 'vue-router'
import HomePageView from '../views/HomePageView.vue'
import MessagePageView from '../views/MessagePageView.vue'
import AboutPageView from '../views/AboutPageView.vue'
import PostPageView from '../views/PostPageView.vue'

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomePageView
  },
  {
    path: '/message',
    name: 'message',
    component: MessagePageView
  },
  {
    path: '/about',
    name: 'about',
    component: AboutPageView
  },
  {
    path: '/posts/:id',
    name: 'post',
    component: PostPageView
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
