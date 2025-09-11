/**
 * 自定义BPMN调色板提供器 - 中文版
 * 用于自定义工具栏中的元素和标签
 */

export default function PaletteProvider(palette, create, elementFactory, spaceTool, lassoTool, handTool, globalConnect, translate) {
  this._palette = palette
  this._create = create
  this._elementFactory = elementFactory
  this._spaceTool = spaceTool
  this._lassoTool = lassoTool
  this._handTool = handTool
  this._globalConnect = globalConnect
  this._translate = translate

  palette.registerProvider(this)
}

PaletteProvider.$inject = [
  'palette',
  'create',
  'elementFactory',
  'spaceTool',
  'lassoTool',
  'handTool',
  'globalConnect',
  'translate'
]

PaletteProvider.prototype.getPaletteEntries = function(element) {
  const actions = {}
  const create = this._create
  const elementFactory = this._elementFactory
  const spaceTool = this._spaceTool
  const lassoTool = this._lassoTool
  const handTool = this._handTool
  const globalConnect = this._globalConnect
  const translate = this._translate

  function createAction(type, group, className, title, options) {
    function createListener(event) {
      const shape = elementFactory.createShape(Object.assign({ type: type }, options))
      if (options) {
        shape.businessObject.di.isExpanded = options.isExpanded
      }
      create.start(event, shape)
    }

    const shortType = type.replace(/^bpmn:/, '')

    return {
      group: group,
      className: className,
      title: title || translate('Create {type}', { type: shortType }),
      action: {
        dragstart: createListener,
        click: createListener
      }
    }
  }

  function createSubprocess(event) {
    const subProcess = elementFactory.createShape({
      type: 'bpmn:SubProcess',
      x: 0,
      y: 0,
      isExpanded: true
    })

    const startEvent = elementFactory.createShape({
      type: 'bpmn:StartEvent',
      x: 40,
      y: 82,
      parent: subProcess
    })

    create.start(event, [subProcess, startEvent])
  }

  function createParticipant(event) {
    create.start(event, elementFactory.createParticipantShape())
  }

  Object.assign(actions, {
    'hand-tool': {
      group: 'tools',
      className: 'bpmn-icon-hand-tool',
      title: translate('激活手型工具'),
      action: {
        click: function(event) {
          handTool.activateHand(event)
        }
      }
    },
    'lasso-tool': {
      group: 'tools',
      className: 'bpmn-icon-lasso-tool',
      title: translate('激活套索工具'),
      action: {
        click: function(event) {
          lassoTool.activateSelection(event)
        }
      }
    },
    'space-tool': {
      group: 'tools',
      className: 'bpmn-icon-space-tool',
      title: translate('激活创建/删除空间工具'),
      action: {
        click: function(event) {
          spaceTool.activateSelection(event)
        }
      }
    },
    'global-connect-tool': {
      group: 'tools',
      className: 'bpmn-icon-connection-multi',
      title: translate('激活全局连接工具'),
      action: {
        click: function(event) {
          globalConnect.toggle(event)
        }
      }
    },
    'tool-separator': {
      group: 'tools',
      separator: true
    },
    'create.start-event': createAction(
      'bpmn:StartEvent', 'event', 'bpmn-icon-start-event-none',
      translate('创建开始事件')
    ),
    'create.intermediate-event': createAction(
      'bpmn:IntermediateThrowEvent', 'event', 'bpmn-icon-intermediate-event-none',
      translate('创建中间事件')
    ),
    'create.end-event': createAction(
      'bpmn:EndEvent', 'event', 'bpmn-icon-end-event-none',
      translate('创建结束事件')
    ),
    'create.exclusive-gateway': createAction(
      'bpmn:ExclusiveGateway', 'gateway', 'bpmn-icon-gateway-none',
      translate('创建排他网关')
    ),
    'create.task': createAction(
      'bpmn:Task', 'activity', 'bpmn-icon-task',
      translate('创建任务')
    ),
    'create.user-task': createAction(
      'bpmn:UserTask', 'activity', 'bpmn-icon-user-task',
      translate('创建用户任务')
    ),
    'create.service-task': createAction(
      'bpmn:ServiceTask', 'activity', 'bpmn-icon-service-task',
      translate('创建服务任务')
    ),
    'create.script-task': createAction(
      'bpmn:ScriptTask', 'activity', 'bpmn-icon-script-task',
      translate('创建脚本任务')
    ),
    'create.business-rule-task': createAction(
      'bpmn:BusinessRuleTask', 'activity', 'bpmn-icon-business-rule-task',
      translate('创建业务规则任务')
    ),
    'create.receive-task': createAction(
      'bpmn:ReceiveTask', 'activity', 'bpmn-icon-receive-task',
      translate('创建接收任务')
    ),
    'create.send-task': createAction(
      'bpmn:SendTask', 'activity', 'bpmn-icon-send-task',
      translate('创建发送任务')
    ),
    'create.manual-task': createAction(
      'bpmn:ManualTask', 'activity', 'bpmn-icon-manual-task',
      translate('创建手工任务')
    ),
    'create.call-activity': createAction(
      'bpmn:CallActivity', 'activity', 'bpmn-icon-call-activity',
      translate('创建调用活动')
    ),
    'create.subprocess-expanded': {
      group: 'activity',
      className: 'bpmn-icon-subprocess-expanded',
      title: translate('创建展开的子流程'),
      action: {
        dragstart: createSubprocess,
        click: createSubprocess
      }
    },
    'create.participant-expanded': {
      group: 'collaboration',
      className: 'bpmn-icon-participant',
      title: translate('创建池/参与者'),
      action: {
        dragstart: createParticipant,
        click: createParticipant
      }
    },
    'create.group': createAction(
      'bpmn:Group', 'artifact', 'bpmn-icon-group',
      translate('创建组')
    ),
    'create.data-object': createAction(
      'bpmn:DataObjectReference', 'data', 'bpmn-icon-data-object',
      translate('创建数据对象')
    ),
    'create.data-store': createAction(
      'bpmn:DataStoreReference', 'data', 'bpmn-icon-data-store',
      translate('创建数据存储')
    ),
    'create.text-annotation': createAction(
      'bpmn:TextAnnotation', 'artifact', 'bpmn-icon-text-annotation',
      translate('创建文本注释')
    )
  })

  return actions
}

/**
 * 调色板提供器模块
 */
export const paletteProviderModule = {
  __init__: ['paletteProvider'],
  paletteProvider: ['type', PaletteProvider]
}