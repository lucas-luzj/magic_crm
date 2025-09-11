const reduceTree = function(nodes){
    return nodes.map(child => {
        let children = child.children ? reduceTree(child.children) : [];
        return {value:child.id, label:child.name, children:children}
    })
}
export default {
    title: "用户管理",
    search: [
        { code: "departmentId" },
        { code: "keyword", label:"姓名,电话,email", type:"string", control:"text"},
    ],
    call: {
        list: "/api/users/list",
        get: "/api/users/get",
        update: "/api/users/update",
        delete: "/api/users/delete",
    },
    columns: [
        { code: "id", label: "ID", type: "int", control: "text", editable: false },

        { code: "username", label: "登录名称", type: "string", control: "text", editable: true,required:true },
    
        { code: "fullName", label: "姓名", type: "string", control: "text", editable: true,required:true},
        { code: "namePinyin", label: "拼音首字母", type: "string", control: "text" },
        { code: "departmentId", label: "部门", type: "string", control: "combo", editable: true,
            options(){
                return $server.call("/api/departments/tree").then(res=>{
                    return reduceTree(res)
                })
            }
        },
        { code: "departmentName", label: "部门名称", type: "string", control: "text" },
        { code: "email", label: "电子邮件", type: "string", control: "text", editable: true ,required:true},
        { code: "avatar", label: "头像", type: "string", control: "photo", editable: true,
            photo:{max:1}
         },
        { code: "phoneNumber", label: "手机号码", type: "string", control: "text", editable: true,required:true },
        { code: "isActive", label: "启用", type: "boolean", control: "check", editable: true,required:true,default:true },
        { code: "lastLoginAt", label: "最后登录时间", type: "datetime", control: "datetime" },
        
        // 新增字段
        { code: "photo", label: "照片", type: "string", control: "photo", editable: true,
            photo:{max:1}
         },
        { code: "birthday", label: "生日", type: "date", control: "date", editable: true },
        { code: "position", label: "职位", type: "string", control: "text", editable: true },
        { code: "gender", label: "性别", type: "int", control: "combo", editable: true,
            options: [
                { value: 0, label: "未知" },
                { value: 1, label: "男" },
                { value: 2, label: "女" }
            ]
        },
        { code: "hometown", label: "籍贯", type: "string", control: "text", editable: true },
        { code: "graduateSchool", label: "毕业院校", type: "string", control: "text", editable: true },
        { code: "address", label: "地址", type: "string", control: "textarea", editable: true },
        { code: "hireDate", label: "入职日期", type: "date", control: "date", editable: true },
        { code: "resignationDate", label: "离职日期", type: "date", control: "date", editable: true },
        { code: "emergencyContact", label: "紧急联系人", type: "string", control: "text", editable: true },
        { code: "emergencyContactPhone", label: "紧急联系人电话", type: "string", control: "text", editable: true },
        { code: "wechatUnionId", label: "微信UnionID", type: "string", control: "text", editable: true },
        { code: "wechatOpenId", label: "微信OpenID", type: "string", control: "text", editable: true },
        { code: "dingtalkId", label: "钉钉ID", type: "string", control: "text", editable: true },
        { code: "qqNumber", label: "QQ号码", type: "string", control: "text", editable: true },
        { code: "lastLoginIp", label: "最后登录IP", type: "string", control: "text", editable: false },
        { code: "lastLoginUserAgent", label: "最后登录设备", type: "string", control: "textarea", editable: false },
        { code: "remarks", label: "备注", type: "string", control: "textarea", editable: true },
       
    ],
    grid: "id,departmentName,username,fullName,namePinyin,email,avatar,phoneNumber,position,gender,isActive,lastLoginAt",
    cell: {
        title: "${Date1} - ${Text2}",
        label: "${Text23}: 检查部门：${DeptName}",
        icon: "Text3"
    },
    search_action: [
        //{ label: "导出", click:"onExport", type:"primary"}
    ],
    
    form: {
        width: "600px",
        label_position: "top",
        label_width: "80px",
        actions: [
        ],
        items:{
            tabs:[
                {tab:"基础信息",items:"username:12,fullName:12,phoneNumber:12,gender:12,departmentId:12,position:12,birthday:12,hometown:12,email,avatar:12,photo:12,address,isActive:12"},
                {tab:"扩展信息",items:"graduateSchool:12,hireDate:12,resignationDate:12,emergencyContact:12,emergencyContactPhone:12,qqNumber:12,remarks,"},
                {tab:"其他信息",items:"lastLoginAt:12,lastLoginIp:12,lastLoginUserAgent"}
            ]
        },
        vue: {
            methods: {

            },
            computed: {
                allowSave() {
                    return this.formData.ID >= 0 || this.formData.CreateUserID == $store.user.uid;
                }
            }
        }
    }

};