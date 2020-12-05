<template>
    <div>
        <div class="container">
            <div class="handle-box">   
              <el-input v-model="query.app" placeholder="应用名称" class="handle-input mr10"></el-input>             
              <el-input v-model="query.nodeIp" placeholder="节点名称" class="handle-input mr10" style="width:120px"></el-input>
              <el-select v-model="query.logLevel" placeholder="日志级别" clearable class="handle-select mr10" style="width:100px">
                  <el-option 
                    v-for="item in options" 
                    :key="item.value"
                    :label="item.label"
                    :value="item.value">
                  </el-option>
                </el-select>
                <el-date-picker
                  v-model="query.startDate"
                  type="datetime"
                  format="yyyy-MM-dd HH:mm:ss"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  placeholder="开始日期" style="width:185px">
                </el-date-picker>
                <el-date-picker class="mr10"
                  v-model="query.endDate"
                  type="datetime"
                  format="yyyy-MM-dd HH:mm:ss"
                  value-format="yyyy-MM-dd HH:mm:ss"
                  placeholder="结束日期" style="width:185px">
                </el-date-picker>
              <el-input v-model="query.content" placeholder="日志内容" class="handle-input mr10" style="width:200px"></el-input>
              <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>

              <div style="float: right;">
                <div>
                  <el-checkbox v-model="appsld">应用名称</el-checkbox>
                  <el-checkbox v-model="nodesld">节点名称</el-checkbox>
                  <el-checkbox v-model="levelsld">日志级别</el-checkbox>
                  <el-checkbox v-model="threadsld">线程名称</el-checkbox>
                  <el-checkbox v-model="loggerNameld">类名称</el-checkbox>
                </div>
                <div>
                  <i class="el-icon-delete" @click="clear"></i>
                </div>
               
              </div>
            </div>
            

            <div class="infinite-list-wrapper" :style="defaultHeight">
              <ul ref="uiscroll"
                class="list log-style"
                v-infinite-scroll="load"
                infinite-scroll-disabled="disabled">
                <!--<li @dblclick="drawerOpen(msg)" v-for="(msg , index) in messages" :key="index" class="list-item">
                  <p v-html="logSplice(msg)"/>
                </li>-->
                 <li v-for="i in count" class="list-item">{{ i }}</li>
              </ul>
              <p v-if="loading" class="end"><i class="el-icon-loading"/>加载中...</p>
              <p v-if="noMore" class="end">没有更多了</p>
            </div>

        </div>

      <el-drawer
        title="查看单条信息"
        :visible.sync="drawer"
        :direction="direction"
        :with-header="false"
        size="40%"
        :before-close="handleClose">
          <div style="padding:15px;">
              <json-viewer v-if="drawer" :value="jsonData" :expand-depth=5 copyable sort boxed></json-viewer>
              <div class="stackTrace" v-html="stackTrace"
                v-loading="drawerloading"
                element-loading-text="拼命加载中"
                element-loading-spinner="el-icon-loading"
                element-loading-background="rgba(0, 0, 0, 0.8)">
              </div>
          </div>
      </el-drawer>  
    </div>
</template>

<script>
  import { fetchRunLog, findById } from '../../api/index';

  export default {
    data () {
      return {
        defaultHeight: {
            height: ""
        },
        appsld:true,
        nodesld:true,
        levelsld:true,
        threadsld:true,
        loggerNameld:true,

        options: [{
          value: 'DEBUG',
          label: 'DEBUG'
        }, {
          value: 'INFO',
          label: 'INFO'
        }, {
          value: 'WARN',
          label: 'WARN'
        }, {
          value: 'ERROR',
          label: 'ERROR'
        }, {
          value: 'FATAL',
          label: 'FATAL'
        }],
        count: 0,
        messages:[],
        loading: false,
        //Drawer 抽屉
        drawer: false,
        direction: 'rtl',
        jsonData:null,
        stackTrace:'',
        drawerloading:false,

        nodataTimes:0,
        timer:'',
        query: {
                app: '',
                logLevel: '',
                nodeIp:'',
                content:'',
                startDate:'',
                endDate:'',
                size: 20,
                skip: 0
            },
      }
    },
    computed: {
       noMore () {
        return this.count >= 2000
      },
      disabled () {
        return this.loading || this.noMore
      },

      logSplice(){
        return function(item){
          let msg = "";
          if(this.appsld){
            msg =   `<span style='color: green;padding-right: 10px; '>${item.app}</span>`
          }
          if(this.nodesld){
            msg =  `${msg}<span style='color: coral;padding-right: 10px;'>${item.nodeIp}</span>`
          }
          msg = `${msg}<span style='color: burlywood;padding-right: 10px;'>${item.createDate}</span>`
          if(this.levelsld){
            msg = `${msg}<span style='color: royalblue;padding-right: 10px;'>${item.logLevel}</span>`
          }
          if(this.threadsld){
            msg = `${msg}<span style='color: blueviolet;padding-right: 10px;'>[ ${item.threadName} ]</span>`
          }          
          if(this.loggerNameld){
            msg = `${msg}<span style='color: cadetblue;padding-right: 10px;'>${item.loggerName}</span>`
          }
          
          msg = `${msg}<span style='color: darkorange;padding-right: 10px;'>${item.lineNumber}</span>`
          msg = `${msg}<span style='color: white;padding-right: 10px;'>${item.content}</span>`
          return msg;
        }
      }
    },
    methods: {
      // 触发搜索按钮
        handleSearch() {
          
            this.$set(this.query, 'skip', 0);
            //this.messages=[];       
            this.count=4;
        //document.body.scrollTop=document.documentElement.scrollTop=0;
            //this.load ();
            this.loading = true

      
          
          //this.load0();
          if(this.timer!=""){
            clearInterval(this.timer);
            this.timer="";
          }
          this.timer = setInterval(this.load0, 1000);
          console.log(" timer...::"+this.timer);
        },

      load0(){
        
        let height= this.$refs.uiscroll.offsetHeight;
        let cheight = this.defaultHeight.height.replace("px","");

          
//this.loading = false;
          if(height>parseInt(cheight)){
             clearInterval(this.timer);
             this.timer="";
             console.log("remove interval...::"+this.timer);
             this.loading = false;
          }
          
          this.count+=5;
          
          console.log("defaultHeight::"+this.defaultHeight.height+" ul height:"+height);

      },


      load () {
        this.loading = true
        setTimeout(() => {
          this.count += 5
          this.loading = false
        }, 1000)
      },

      clear(){
        this.messages=[];
        this.count=4;
        this.loading = true
      },


      //定义方法，获取高度减去头尾
      getHeight() {
          this.defaultHeight.height = (window.innerHeight - 175)+"px";
      },
      handleClose(done) {
        this.drawerloading = false;
        this.jsonData = null;
        this.stackTrace = "";
        done();
      },

      drawerOpen(vo){
        this.drawer = true;
        this.jsonData = vo;
        this.drawerloading = true;
        findById(vo.id).then(res=>{          
          this.drawerloading = false;
          let st = res.data.stackTrace;
          if(st){
            st = st.replace(/\r\n/g,"<br>");
            st = st.replace(/\t/g,"&nbsp;&nbsp;");
            this.stackTrace = st;
            res.data.stackTrace="...";
          }
          
          this.jsonData = res.data;
          
        });
      }
    },

    created() {
      //页面创建时执行一次getHeight进行赋值，顺道绑定resize事件
      window.addEventListener("resize", this.getHeight);
      this.getHeight();
    }
  }
</script>
<style scoped>
    .infinite-list-wrapper{
      margin: 10px auto;
      background: #242f42;
      color: #dadada;
      overflow: auto;
      /*height: 100%;*/
      height: calc(100vh - 152px);
    }
    .infinite-list-wrapper .list-item {
        display: flex;
        padding: 3px 5px;
        border-bottom: 1px solid #5c5c5d;
        white-space: nowrap;
        font-size: 16px;  
    }
    .infinite-list-wrapper .end {
      align-items: center;
      justify-content: center;
      padding: 10px;
      display: flex;
      -webkit-box-align: center;
      font-size: 12px;
    }
    
    .handle-box {
        margin-bottom: 0px;
    }

    .handle-select {
        width: 120px;
    }

    .handle-input {
        width: 180px;
        display: inline-block;
    }
    .container {
      padding: 10px;
    }
  .mr10 {
    margin-right: 10px;
  }
  .el-checkbox {
    margin-right: 15px;
  }
  .el-checkbox__label {
    padding-left: 5px;
  }
  .stackTrace{
    padding: 10px;
  }
  .el-icon-delete:hover{
    color: #409eff;
  }
</style>