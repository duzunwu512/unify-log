<template>
    <div>
        <div class="crumbs">
            <el-breadcrumb separator="/">
                <el-breadcrumb-item>
                    <i class="el-icon-lx-cascades"></i> 基础表格
                </el-breadcrumb-item>
            </el-breadcrumb>
        </div>
        <div class="container">
            <div class="handle-box">
                <el-button
                    type="primary"
                    icon="el-icon-delete"
                    class="handle-del mr10"
                    @click="delAllSelection"
                >批量删除</el-button>
                <!--<el-select v-model="query.address" placeholder="地址" class="handle-select mr10">
                    <el-option key="1" label="广东省" value="广东省"></el-option>
                    <el-option key="2" label="湖南省" value="湖南省"></el-option>
                </el-select>-->

                <el-input v-model="query.createrRealName" placeholder="操作人" class="handle-input mr10" style="width:120px"></el-input>
                <el-input v-model="query.business" placeholder="业务" class="handle-input mr10" style="width:120px"></el-input>
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
                <el-button type="primary" icon="el-icon-search" @click="handleSearch">搜索</el-button>
            </div>
            <el-table
                :data="tableData"
                border
                class="table"
                ref="multipleTable"
                header-cell-class-name="table-header"
                @selection-change="handleSelectionChange"
            >
                <el-table-column type="selection" width="55" align="center"></el-table-column>           
                <el-table-column prop="createrRealName" width="120" label="操作人"></el-table-column>                
                <el-table-column prop="createrDeptName" width="150" label="操作人部门"></el-table-column>
                <el-table-column prop="createDate" width="180" label="操作时间"></el-table-column>
                <el-table-column prop="business" width="150" label="业务"></el-table-column>
                <el-table-column prop="operateType" width="120" label="操作类型"></el-table-column>
                <el-table-column prop="content" label="内容" show-overflow-tooltip></el-table-column>
                <el-table-column label="操作" width="150" align="center">
                    <template slot-scope="scope">
                        <el-button
                            type="text"
                            icon="el-icon-edit"
                            @click="handleEdit(scope.$index, scope.row)"
                        >编辑</el-button>
                        <el-button
                            type="text"
                            icon="el-icon-delete"
                            class="red"
                            @click="handleDelete(scope.$index, scope.row)"
                        >删除</el-button>
                    </template>
                </el-table-column>
            </el-table>
            <div class="pagination">
                <el-pagination
                    background
                    layout="total, prev, pager, next"
                    :current-page="query.page"
                    :page-size="query.size"
                    :total="totalElements"
                    @current-change="handlePageChange"
                ></el-pagination>
            </div>
        </div>

        <!-- 编辑弹出框 -->
        <el-dialog title="编辑" :visible.sync="editVisible" width="30%">
            <el-form ref="form" :model="form" label-width="100px">
                <el-form-item label="用户帐户">
                    <el-input v-model="form.createrUserName"></el-input>
                </el-form-item>
                <el-form-item label="用户名">
                    <el-input v-model="form.createrRealName"></el-input>
                </el-form-item>
                <el-form-item label="业务">
                    <el-input v-model="form.business"></el-input>
                </el-form-item>
                <el-form-item label="操作类别">
                    <el-input v-model="form.operateType"></el-input>
                </el-form-item>
                <el-form-item label="(子)公司名称">
                    <el-input v-model="form.companyName"></el-input>
                </el-form-item>
                <el-form-item label="内容">
                    <el-input v-model="form.content"></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="editVisible = false">取 消</el-button>
                <el-button type="primary" @click="saveEdit">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
import { fetchBizLog, deleteBizLog, updateBizLog, findBizLog } from '../../api/index';
export default {
    name: 'basetable',
    data() {
        return {
            query: {
                business: '',
                createrRealName: '',
                startDate:'',
                endDate:'',
                page: 1,
                size: 10
            },
            tableData: [],
            multipleSelection: [],
            editVisible: false,
            totalElements: 0,
            form: {},
            idx: -1,
            id: -1
        };
    },
    created() {
        this.getData();
    },
    methods: {
        // 获取 easy-mock 的模拟数据
        getData() {
            fetchBizLog(this.query).then(res => {
                this.tableData = res.content;
                this.totalElements = res.totalElements || 0;
            });
        },

        del(id){
            // 二次确认删除
            this.$confirm('确定要删除吗？', '提示', {
                type: 'warning'
            }).then(() => {
                deleteBizLog(id)
                .then(res => {
                    if(res.resultCode=="200"){
                        this.$message.success(`删除成功`);
                        this.getData();
                    }else{
                        this.$message.error(`删除失败！`);
                    }
                    
                    });
                })
            .catch(() => {});
        },
        // 触发搜索按钮
        handleSearch() {
            this.$set(this.query, 'page', 1);
            this.getData();
        },
        // 删除操作
        handleDelete(index, row) {
            this.del(row.id);
        },
        // 多选操作
        handleSelectionChange(val) {
            this.multipleSelection = val;
        },
        delAllSelection() {
            const length = this.multipleSelection.length;
            if(length<1){
                this.$message.info(`请选择要删除的数据！`);
                return;
            }
            let str="";
            for (let i = 0; i < length; i++) {
                str += this.multipleSelection[i].id + ',';
            }
            this.del(str);
        },
        // 编辑操作
        handleEdit(index, row) {
            this.idx = index;
            this.form = row;
            this.editVisible = true;
        },
        // 保存编辑
        saveEdit() {            
            updateBizLog(this.form).then(res => {
                if(res.data){
                    this.editVisible = false;
                    this.$message.success(`数据保存成功！！！`);
                    this.$set(this.tableData, this.idx, this.form);
                }else{
                    this.$message.error(`数据保存失败！！！`);
                }
            });

            
        },
        // 分页导航
        handlePageChange(val) {
            this.$set(this.query, 'page', val);
            this.getData();
        }
    }
};
</script>

<style scoped>
.handle-box {
    margin-bottom: 20px;
}

.handle-select {
    width: 120px;
}

.handle-input {
    width: 300px;
    display: inline-block;
}
.table {
    width: 100%;
    font-size: 14px;
}
.red {
    color: #ff0000;
}
.mr10 {
    margin-right: 10px;
}
.table-td-thumb {
    display: block;
    margin: auto;
    width: 40px;
    height: 40px;
}
</style>
