const reduceTree = function(nodes){
    return nodes.map(child => {
        let children = child.children ? reduceTree(child.children) : [];
        return {value:child.id, label:child.name, children:children}
    })
}
export default {
    title: "部门管理",
    search: [
        { code: "keyword",label:"名称",type:"string",control:"text" },
    ],
    call: {
        list: "/api/departments/tree",
        get: "/api/departments/get",
        update: "/api/departments/update",
        delete: "/api/departments/delete",
    },
    columns: [
        { code: "id", label: "ID", type: "int", control: "text", editable: false,align:"left" },

        { code: "name", label: "部门名称", type: "string", control: "text", editable: true, required: true },
        { code: "code", label: "部门编码", type: "string", control: "text", editable: true },
        {
            code: "parentId", searchCode:"pid", label: "上级部门", type: "int", control: "combo", editable: true,
            options() {
                return $server.call("/api/departments/tree").then(res => {
                    return reduceTree(res)
                })
            }
        },
        { code: "parentName", label: "上级部门", type: "string", control: "text", editable: false },
        {
            code: "managerId", label: "负责人", type: "int", control: "selectinput", editable: true,
            selectinput: {
                selectCfg: {
                    tbname: "User",
                    srcFields: "id,fullName",
                    destFields: "managerId,managerName",
                },
                labelField: "managerName",
            }
        },
        { code: "managerName", label: "负责人", type: "string", control: "text", editable: false },
        { code: "description", label: "部门描述", type: "string", control: "textarea", editable: true },
        { code: "sortOrder", label: "排序号", type: "int", control: "number", editable: true, default: 0 },
        { code: "isActive", label: "启用状态", type: "boolean", control: "check", editable: true, required: true, default: true },
        { code: "createdAt", label: "创建时间", type: "datetime", control: "datetime", editable: false },
        { code: "updatedAt", label: "更新时间", type: "datetime", control: "datetime", editable: false },
        { code: "createdBy", label: "创建人ID", type: "int", control: "text", editable: false },
        { code: "updatedBy", label: "更新人ID", type: "int", control: "text", editable: false },

    ],
    grid: {
        fields: "id:100,name,code:100,parentName:120,managerName:100,isActive:80,createdAt:140,updatedAt:140",
        tree:{
            id: "id",
            pid:"parentId",
            children: 'children',
            load(row, treeNode, resolve, search) 
            {
                let pm = { ...search, pid: row.id, pageIndex:1, pageSize:1000 };
                $server.call("/api/departments/list", pm).then(res => {
                    resolve(res.records);
                });
            }
        }
    },
    cell: {
        title: "${name}",
        label: "负责人：${managerName} | 状态：${isActive}",
        icon: "name"
    },
    search_action: [
        //{ label: "导出", click:"onExport", type:"primary"}
    ],

    form: {
        width: "600px",
        label_position: "right",
        label_width: "80px",
        actions: [
        ],
        items: "name:12,code:12,parentId:12,managerId:12,description,sortOrder:12,isActive:12",
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