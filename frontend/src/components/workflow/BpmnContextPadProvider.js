/**
 * 自定义BPMN上下文菜单提供器 - 中文版
 * 用于自定义右键菜单和快捷操作的中文标签
 */

export default function ContextPadProvider(contextPad, config, injector, translate, bpmnFactory, elementFactory, create, modeling, connect, rules) {
  this.create = create
  this.elementFactory = elementFactory
  this.translate = translate
  this.modeling = modeling
  this.connect = connect
  this.rules = rules
  this.bpmnFactory = bpmnFactory

  if (config.autoPlace !== false) {
    this.autoPlace = injector.get('autoPlace', false)
  }

  contextPad.registerProvider(this)
}

ContextPadProvider.$inject = [
  'contextPad',
  'config',
  'injector',
  'translate',
  'bpmnFactory',
  'elementFactory',
  'create',
  'modeling',
  'connect',
  'rules'
]

ContextPadProvider.prototype.getContextPadEntries = function(element) {
  const contextPad = this
  const modeling = this.modeling
  const elementFactory = this.elementFactory
  const connect = this.connect
  const create = this.create
  const bpmnFactory = this.bpmnFactory
  const rules = this.rules
  const autoPlace = this.autoPlace
  const translate = this.translate

  const actions = {}

  if (element.type === 'label') {
    return actions
  }

  const businessObject = element.businessObject

  function startConnect(event, element) {
    connect.start(event, element)
  }

  function removeElement() {
    modeling.removeElements([element])
  }

  function getReplaceMenuPosition(element) {
    const Y_OFFSET = 5
    const diagramContainer = contextPad._canvas.getContainer()
    const pad = contextPad.getPad(element).html

    const diagramRect = diagramContainer.getBoundingClientRect()
    const padRect = pad.getBoundingClientRect()

    const top = padRect.top - diagramRect.top
    const left = padRect.left - diagramRect.left

    const pos = {
      x: left,
      y: top + padRect.height + Y_OFFSET
    }

    return pos
  }

  function appendAction(type, className, title, options) {
    if (typeof title !== 'string') {
      options = title
      title = translate('追加 {type}', { type: type.replace(/^bpmn:/, '') })
    }

    function appendStart(event) {
      const shape = elementFactory.createShape(Object.assign({ type: type }, options))
      create.start(event, shape, {
        source: element
      })
    }

    const append = autoPlace ? function(event) {
      const shape = elementFactory.createShape(Object.assign({ type: type }, options))
      autoPlace.append(element, shape)
    } : appendStart

    return {
      group: 'model',
      className: className,
      title: title,
      action: {
        dragstart: appendStart,
        click: append
      }
    }
  }

  function splitLaneHandler(count) {
    return function(event) {
      // 分割泳道的逻辑
      modeling.splitLane(element, count)
    }
  }

  // 删除操作
  if (rules.allowed('elements.delete', { elements: [element] })) {
    actions['delete'] = {
      group: 'edit',
      className: 'bpmn-icon-trash',
      title: translate('删除'),
      action: {
        click: removeElement
      }
    }
  }

  // 连接操作
  if (rules.allowed('connection.create', { source: element })) {
    actions['connect'] = {
      group: 'connect',
      className: 'bpmn-icon-connection-multi',
      title: translate('连接使用顺序流或消息流或关联'),
      action: {
        click: startConnect,
        dragstart: startConnect
      }
    }
  }

  // 根据元素类型添加特定的上下文菜单项
  if (businessObject.$instanceOf('bpmn:FlowNode')) {
    if (businessObject.$instanceOf('bpmn:EventBasedGateway')) {
      actions['append.receive-task'] = appendAction('bpmn:ReceiveTask', 'bpmn-icon-receive-task', translate('追加接收任务'))
      actions['append.message-intermediate-event'] = appendAction('bpmn:IntermediateCatchEvent', 'bpmn-icon-intermediate-event-catch-message', translate('追加消息中间捕获事件'), { eventDefinitionType: 'bpmn:MessageEventDefinition' })
      actions['append.timer-intermediate-event'] = appendAction('bpmn:IntermediateCatchEvent', 'bpmn-icon-intermediate-event-catch-timer', translate('追加定时器中间捕获事件'), { eventDefinitionType: 'bpmn:TimerEventDefinition' })
      actions['append.condition-intermediate-event'] = appendAction('bpmn:IntermediateCatchEvent', 'bpmn-icon-intermediate-event-catch-condition', translate('追加条件中间捕获事件'), { eventDefinitionType: 'bpmn:ConditionalEventDefinition' })
      actions['append.signal-intermediate-event'] = appendAction('bpmn:IntermediateCatchEvent', 'bpmn-icon-intermediate-event-catch-signal', translate('追加信号中间捕获事件'), { eventDefinitionType: 'bpmn:SignalEventDefinition' })
    } else if (businessObject.$instanceOf('bpmn:BoundaryEvent')) {
      // 边界事件的特殊处理
    } else if (businessObject.$instanceOf('bpmn:StartEvent')) {
      actions['append.user-task'] = appendAction('bpmn:UserTask', 'bpmn-icon-user-task', translate('追加用户任务'))
      actions['append.service-task'] = appendAction('bpmn:ServiceTask', 'bpmn-icon-service-task', translate('追加服务任务'))
      actions['append.script-task'] = appendAction('bpmn:ScriptTask', 'bpmn-icon-script-task', translate('追加脚本任务'))
      actions['append.business-rule-task'] = appendAction('bpmn:BusinessRuleTask', 'bpmn-icon-business-rule-task', translate('追加业务规则任务'))
      actions['append.receive-task'] = appendAction('bpmn:ReceiveTask', 'bpmn-icon-receive-task', translate('追加接收任务'))
      actions['append.send-task'] = appendAction('bpmn:SendTask', 'bpmn-icon-send-task', translate('追加发送任务'))
      actions['append.manual-task'] = appendAction('bpmn:ManualTask', 'bpmn-icon-manual-task', translate('追加手工任务'))
      actions['append.call-activity'] = appendAction('bpmn:CallActivity', 'bpmn-icon-call-activity', translate('追加调用活动'))
      actions['append.exclusive-gateway'] = appendAction('bpmn:ExclusiveGateway', 'bpmn-icon-gateway-none', translate('追加排他网关'))
      actions['append.end-event'] = appendAction('bpmn:EndEvent', 'bpmn-icon-end-event-none', translate('追加结束事件'))
    } else if (businessObject.$instanceOf('bpmn:Task') || businessObject.$instanceOf('bpmn:CallActivity')) {
      actions['append.user-task'] = appendAction('bpmn:UserTask', 'bpmn-icon-user-task', translate('追加用户任务'))
      actions['append.service-task'] = appendAction('bpmn:ServiceTask', 'bpmn-icon-service-task', translate('追加服务任务'))
      actions['append.script-task'] = appendAction('bpmn:ScriptTask', 'bpmn-icon-script-task', translate('追加脚本任务'))
      actions['append.business-rule-task'] = appendAction('bpmn:BusinessRuleTask', 'bpmn-icon-business-rule-task', translate('追加业务规则任务'))
      actions['append.receive-task'] = appendAction('bpmn:ReceiveTask', 'bpmn-icon-receive-task', translate('追加接收任务'))
      actions['append.send-task'] = appendAction('bpmn:SendTask', 'bpmn-icon-send-task', translate('追加发送任务'))
      actions['append.manual-task'] = appendAction('bpmn:ManualTask', 'bpmn-icon-manual-task', translate('追加手工任务'))
      actions['append.call-activity'] = appendAction('bpmn:CallActivity', 'bpmn-icon-call-activity', translate('追加调用活动'))
      actions['append.exclusive-gateway'] = appendAction('bpmn:ExclusiveGateway', 'bpmn-icon-gateway-none', translate('追加排他网关'))
      actions['append.parallel-gateway'] = appendAction('bpmn:ParallelGateway', 'bpmn-icon-gateway-parallel', translate('追加并行网关'))
      actions['append.inclusive-gateway'] = appendAction('bpmn:InclusiveGateway', 'bpmn-icon-gateway-or', translate('追加包容网关'))
      actions['append.intermediate-event'] = appendAction('bpmn:IntermediateThrowEvent', 'bpmn-icon-intermediate-event-none', translate('追加中间抛出事件'))
      actions['append.end-event'] = appendAction('bpmn:EndEvent', 'bpmn-icon-end-event-none', translate('追加结束事件'))
    } else if (businessObject.$instanceOf('bpmn:Gateway')) {
      actions['append.user-task'] = appendAction('bpmn:UserTask', 'bpmn-icon-user-task', translate('追加用户任务'))
      actions['append.service-task'] = appendAction('bpmn:ServiceTask', 'bpmn-icon-service-task', translate('追加服务任务'))
      actions['append.script-task'] = appendAction('bpmn:ScriptTask', 'bpmn-icon-script-task', translate('追加脚本任务'))
      actions['append.business-rule-task'] = appendAction('bpmn:BusinessRuleTask', 'bpmn-icon-business-rule-task', translate('追加业务规则任务'))
      actions['append.receive-task'] = appendAction('bpmn:ReceiveTask', 'bpmn-icon-receive-task', translate('追加接收任务'))
      actions['append.send-task'] = appendAction('bpmn:SendTask', 'bpmn-icon-send-task', translate('追加发送任务'))
      actions['append.manual-task'] = appendAction('bpmn:ManualTask', 'bpmn-icon-manual-task', translate('追加手工任务'))
      actions['append.call-activity'] = appendAction('bpmn:CallActivity', 'bpmn-icon-call-activity', translate('追加调用活动'))
      actions['append.exclusive-gateway'] = appendAction('bpmn:ExclusiveGateway', 'bpmn-icon-gateway-none', translate('追加排他网关'))
      actions['append.parallel-gateway'] = appendAction('bpmn:ParallelGateway', 'bpmn-icon-gateway-parallel', translate('追加并行网关'))
      actions['append.inclusive-gateway'] = appendAction('bpmn:InclusiveGateway', 'bpmn-icon-gateway-or', translate('追加包容网关'))
      actions['append.intermediate-event'] = appendAction('bpmn:IntermediateThrowEvent', 'bpmn-icon-intermediate-event-none', translate('追加中间抛出事件'))
      actions['append.end-event'] = appendAction('bpmn:EndEvent', 'bpmn-icon-end-event-none', translate('追加结束事件'))
    }
  }

  // 泳道相关操作
  if (businessObject.$instanceOf('bpmn:Lane') && !businessObject.$instanceOf('bpmn:InteractionNode')) {
    const childLanes = businessObject.childLaneSet && businessObject.childLaneSet.lanes

    actions['lane-insert-above'] = {
      group: 'lane-insert-above',
      className: 'bpmn-icon-lane-insert-above',
      title: translate('在上方添加泳道'),
      action: {
        click: function(event) {
          modeling.addLane(element, 'top')
        }
      }
    }

    if (childLanes) {
      actions['lane-divide-two'] = {
        group: 'lane-divide',
        className: 'bpmn-icon-lane-divide-two',
        title: translate('分割为两个泳道'),
        action: {
          click: splitLaneHandler(2)
        }
      }

      actions['lane-divide-three'] = {
        group: 'lane-divide',
        className: 'bpmn-icon-lane-divide-three',
        title: translate('分割为三个泳道'),
        action: {
          click: splitLaneHandler(3)
        }
      }
    }

    actions['lane-insert-below'] = {
      group: 'lane-insert-below',
      className: 'bpmn-icon-lane-insert-below',
      title: translate('在下方添加泳道'),
      action: {
        click: function(event) {
          modeling.addLane(element, 'bottom')
        }
      }
    }
  }

  return actions
}

/**
 * 上下文菜单提供器模块
 */
export const contextPadProviderModule = {
  __init__: ['contextPadProvider'],
  contextPadProvider: ['type', ContextPadProvider]
}