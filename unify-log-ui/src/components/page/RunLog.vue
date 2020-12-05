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
              <ul id="uiscroll"
                class="list log-style"
                v-infinite-scroll="autoLoad"
                infinite-scroll-disabled="disabled">
                <li @dblclick="drawerOpen(msg)" v-for="(msg , index) in messages" :key="index" class="list-item">
                  <p v-html="logSplice(msg)"/>
                </li>
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
       <el-backtop target=".infinite-list-wrapper"></el-backtop> 
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
        count: -1,
        messages:[],
        loading: false,
        //Drawer 抽屉
        drawer: false,
        direction: 'rtl',
        jsonData:null,
        stackTrace:'',
        drawerloading:false,

        nodataTimes:0,
        query: {
                app: '',
                level: '',
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
        return (this.count == 0 && !(this.query.endDate==null || this.query.endDate=='')) && !this.loading ;
      },

      disabled () {
        return (this.loading || this.noMore) ;
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
            msg = `${msg}<span style='color: royalblue;padding-right: 10px;'>${item.level}</span>`
          }
          if(this.threadsld){
            msg = `${msg}<span style='color: blueviolet;padding-right: 10px;'>[ ${item.thread} ]</span>`
          }          
          if(this.loggerNameld){
            msg = `${msg}<span style='color: cadetblue;padding-right: 10px;'>${item.logger}</span>`
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
          if(this.query.startDate==='' && this.query.endDate!==''){
            this.$alert("开始日期不可为空", "提示", {
              confirmButtonText: '确定'
            });
            return ;
          }

          if((this.query.startDate && this.query.startDate!=='') && (this.query.endDate && this.query.endDate !=='')){
              var begin=new Date(this.query.startDate.replace(/-/g,"/"));
              var end=new Date(this.query.endDate.replace(/-/g,"/"));
              //js判断日期
              if(begin-end>0){
                this.$alert("结束日期不可小于开始日期！", "提示", { confirmButtonText: '确定' });
                return ;
              }
          }

          if(this.query.startDate==='' && this.query.endDate===''){
            this.$message({ message: '开始日期为空将设置默认值！', type: 'warning' });
          }


          this.loading =true;  
          if(this.timer!=""){
            clearInterval(this.timer);
            this.timer="";
          }
          this.$set(this.query, 'skip', 0);
          this.messages=[];
          this.timer = setInterval(this.manualLoad, 1000);
        },

  //无限滚动加载数据，如第一加载数据为0，则下载自动加延迟+1s,直到10S后，一直持续10，直到加载到数据回归0
      autoLoad () {
        this.loading = true;
        fetchRunLog(this.query).then(res => {
            this.messages = this.messages.concat(res.datas);            
            if(this.query.startDate=="" && this.query.startDate==null){
              this.query.startDate=res.startDate;
            }            
            this.count = res.datas.length;            

            if(res.datas.length<1){
              if(this.nodataTimes<10)   {
                this.nodataTimes++;
              }         
              setTimeout(() => {
                this.loading = false;
                this.count++;                                   
              }, 1000*this.nodataTimes);          
            }else{    
              this.query.skip = this.query.skip+ res.datas.length;  
              this.loading = false;    
              this.nodataTimes=0;
            }
            console.log("autoLoad skip end:::", this.query.skip);
          });
      },

      //手动加载（如：搜索，清空），加载数据满页为至，不然向下滚动不生效，满页后停停止自动加载
      //无限滚动生效
      manualLoad(){
        this.loading =true;
        let height= document.getElementById("uiscroll").offsetHeight;
        let cheight = this.defaultHeight.height.replace("px","");
        if(height>parseInt(cheight)){
          clearInterval(this.timer);
          this.loading =false;
          return;
        }
        fetchRunLog(this.query).then(res => {
          this.messages = this.messages.concat(res.datas);            
          if(this.query.startDate=="" && this.query.startDate==null){
            this.query.startDate=res.startDate;
          }

          this.count = res.datas.length;
          this.loading = false;
          if((this.count == 0 && !(this.query.endDate==null || this.query.endDate==''))){
              clearInterval(this.timer);
              return;
          }
          
          if(res.datas.length>0){
            this.query.skip = this.query.skip+ res.datas.length;
          }            
        });         
          
      },

      //清空日志
      clear(){ 
        this.loading =true;     
        if(this.timer!=""){
          clearInterval(this.timer);
          this.timer="";
        }
        this.count=0;
        this.query.skip = this.query.skip;
        console.log("skip:"+this.query.skip);
        if(this.query.skip-this.query.size>0){
          this.query.skip = this.query.skip- this.query.size/2;
        }else{
          this.query.skip = 0;
        }
        console.log("end skip:"+this.query.skip);
        this.messages=[];
        this.timer = setInterval(this.manualLoad, 1000);

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
          let st = res.data.throwable;
          if(st){
            st = st.replace(/\r\n/g,"<br>");
            st = st.replace(/\t/g,"&nbsp;&nbsp;");
            this.stackTrace = st;
            res.data.throwable="...";
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