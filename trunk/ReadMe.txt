conf 目录为一些xml文件，存储关键词、语法规则等

下面为一些包和类的说明

OIS包
	Analyzer 顶层模块
	StringParser 字符串处理，句子分割、全半角转换

	infoExtract 包 信息抽取
		Template 抽取模板
		TemplateParser 模板匹配算法

	ner包 命名实体识别
		RuleParser 企业名称规则解析
		SuffixParser 后缀词解析
		NEAnalyzer 实体识别

		Hmm包 隐马尔可夫模型

		Constant 包 一些常量和静态变量
