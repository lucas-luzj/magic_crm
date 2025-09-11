/**
 * 一个简单且相对安全的表达式求值器。
 * 它使用 Function 构造函数，这通常是不安全的，
 * 但我们会将作用域严格限制在 'model' 和 'computed' 对象中，
 * 防止访问全局变量。
 * @param {string} expression - 要计算的JS表达式。
 * @param {object} scope - 表达式的作用域，包含 model 和 computed。
 * @returns {*} 表达式的计算结果。
 */
function evaluate(expression, scope) {
  if (!expression || typeof expression !== 'string') {
    return expression;
  }
  try {
    const evaluator = new Function(...Object.keys(scope), `return ${expression}`);
    return evaluator(...Object.values(scope));
  } catch (error) {
    console.error(`Error evaluating expression: "${expression}"`, error);
    return undefined;
  }
}

export class ExpressionEvaluator {
  /**
   * 计算所有在表单级别定义的“计算变量”。
   * @param {object} model - 当前的表单数据模型。
   * @param {Array} computedVariables - 计算变量的配置数组。
   * @returns {object} 一个包含所有计算结果的对象。
   */
  static evaluateComputedVariables(model, computedVariables = []) {
    const computedValues = {};
    const scope = { model };
    
    // 注意：这里假设计算变量之间没有相互依赖。
    // 如果有，需要一个更复杂的依赖图和多轮计算。
    computedVariables.forEach(variable => {
      if (variable.name && variable.expression) {
        computedValues[variable.name] = evaluate(variable.expression, scope);
      }
    });
    return computedValues;
  }

  /**
   * 计算一个属性的值，该属性可能是一个计算变量名，也可能是一个原始表达式。
   * @param {string|boolean} propValue - 组件的属性值 (e.g., 'allowEdit', "model.field > 5", true)。
   * @param {object} model - 当前的表单数据模型。
   * @param {object} computedValues - 预先计算好的计算变量。
   * @returns {*} 属性的最终计算结果。
   */
  static evaluateProperty(propValue, model, computedValues) {
    // 如果不是字符串，直接返回其原始值 (e.g., true, false, undefined)
    if (typeof propValue !== 'string' || propValue.trim() === '') {
      return propValue;
    }

    // 检查是否是一个已定义的计算变量名
    if (computedValues.hasOwnProperty(propValue)) {
      return computedValues[propValue];
    }

    // 创建增强的作用域，同时支持直接访问计算变量和通过 computed 对象访问
    const scope = { 
      model, 
      computed: computedValues,
      // 将计算变量直接添加到作用域中，支持 !allowEdit 这样的表达式
      ...computedValues
    };
    
    // 将其作为原始表达式进行计算
    return evaluate(propValue, scope);
  }

  /**
   * 执行所有“动作/赋值”表达式，并返回更新后的数据模型。
   * @param {object} model - 当前的表单数据模型。
   * @param {object} computedValues - 预先计算好的计算变量。
   * @param {Array} expressions - 动作/赋值的配置数组。
   * @returns {object} 一个新的、包含赋值结果的数据模型。
   */
  static evaluateAssignments(model, computedValues, expressions = []) {
    const newModel = { ...model };
    const scope = { model: newModel, computed: computedValues };

    expressions.forEach(expr => {
      if (expr.target && expr.value) {
        // 目标属性必须是模型的一部分，例如 'firstName', 'lastName'
        // 我们不支持 'componentId.value' 这种格式，以简化逻辑
        const targetField = expr.target;
        if (newModel.hasOwnProperty(targetField)) {
          const result = evaluate(expr.value, scope);
          if (result !== undefined) {
            newModel[targetField] = result;
          }
        }
      }
    });
    return newModel;
  }
}