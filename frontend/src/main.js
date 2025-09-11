import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import { install as LucasMyForm, Server, store, Message, mydate, myEncode } from 'lucas-my-form'
import 'lucas-my-form/dist/lucas-my-form.css'

import App from './App.vue'
import router from './router'
import './styles/index.scss'

const importFunc = new Function('url', `return import(url);`);

const app = createApp(App)
const pinia = createPinia()

function myParseDM(code) {
  code = myEncode.decode(code);
  if (location.hostname == "localhost") {
    const codeUrl = URL.createObjectURL(new Blob([code], { type: 'text/javascript' }));
    return importFunc(codeUrl).then(res => {
      let tb = res.default;
      return tb;
    })
  }
  return new Promise((resolve, reject) => {
    code = code.replace("export default", "return ");
    var f = (new Function(code))();
    proceTbCols(f, cols);
    resolve(f);
  })
}

Server.dm = [];
Server.dms = null;
let dms = localStorage.getItem("_dms_");
if (dms) {
  Server.dms = JSON.parse(dms);
}


store.config({
  UploadServer: "/api/common/uploadFile",
  echartJS: "https://cdn.jsdelivr.net/npm/echarts@6.0.0/dist/echarts.min.js",
  getDM1(tbname) {
    const url = `/dm/${tbname}.js`;
    return importFunc(url).then((module) => {
      const tb = module.default;
      return tb;
    });
  },
  getDM(tbname) {
    tbname = tbname.toLowerCase();
    let key = "dm_" + tbname;
    if (location.hostname != "localhost") {
      let tb = null;
      if (Server.dms) {
        tb = Server.dms[tbname];
        if (!tb) {
          throw new Error("找不到模块：" + tbname);
          return;
        }
      }
      var tb1 = localStorage.getItem(key);
      if (tb1) {
        tb1 = JSON.parse(tb1);
        if (tb && tb1.Hash == tb.Hash && tb1.Tick == tb.Tick) {
          return myParseDM(tb1.code).then(tb2 => {
            if (tbname.startsWith("form3_")) {
              addFile_Archive2Form3(tb2);
            }
            return tb2;
          });
        }
      }
    }
    //本地调试不走缓存通道
    return Server.call("/api/common/getDM", { tbname }).then(res => {
      try {
        localStorage.setItem(key, JSON.stringify(res));
      } catch {
        localStorage.clear();
      }
      return myParseDM(res.Code || res.code);
    });
  }
});

console.log(ElementPlus.version)
app.use(pinia)
app.use(router)
app.use(ElementPlus, {
  locale: zhCn,
})
app.use(LucasMyForm)

// 注册所有图标
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  if (!app.component(key)) {
    app.component(key, component)
  }
}

window.$msg = Message;
window.$msg = Message;
window.$server = Server;
window.$store = store;
window.mydate = mydate;


app.mount('#app')