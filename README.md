# Bukkit&Forge - LuckyBlockPixelmon
- 基于`Pixelmon-1.12.2-8.4.3`开发的`Bukkit`插件，类似于`pokeLucky`模组的功能。 
- 由于 `LuckyBlockPixelmon` 是`Bukkit`插件，可以以`YML`的方式配置并且每个幸运方块的配置独立其中每个配置可以设置传说史诗普通的3种抽奖，其中传说史诗可以单独设置保底的次数。

## 结构
```
LuckyBlockPixelmon/
├── language/
│   └── zh_CN.yml
├── luckyBlock/
│   ├── 娘化方块.yml
│   ├── 幸运方块.yml
│   ├── 异色方块.yml
│   └── 配置示例.yml
├── pixelmonList/
│   ├── alienTextured.yaml
│   ├── allPokemon.yaml
│   ├── alterTextured.yaml
│   ├── ashenTextured.yaml
│   ├── cannotDynamax.yaml
│   ├── creatorTextured.yaml
│   ├── crystalTextured.yaml
│   ├── drownedTextured.yaml
│   ├── genderForm.yaml
│   ├── halloweenForm.yaml
│   ├── illegalShinies.yaml
│   ├── illegalShiniesGalarian.yaml
│   ├── legendaries.yaml
│   ├── mfSprite.yaml
│   ├── mfTextured.yaml
│   ├── onlineTextured.yaml
│   ├── pinkTextured.yaml
│   ├── pokemon.yaml
│   ├── rainbowTextured.yaml
│   ├── spiritTextured.yaml
│   ├── strikeTextured.yaml
│   ├── summerTextured.yaml
│   ├── ultrabeasts.yaml
│   ├── valencianTextured.yaml
│   ├── valentineTextured.yaml
│   └── zombieTextured.yaml
├── userData/
│   └── xxxx-xxxx-xxxx-xxxxxx.json
└── config.yml


```

# 配置示例
```yaml
#
# 配置项严格区分大小写
# 一个配置文件代表绑定一个方块，其它配置绑定同一个方块会按照文件顺序触发逻辑
#
# 可用占位符
# %lbp_PixelName%            # 开出的宝可梦名称
# %lbp_<名称>.legBonus%       # 未触发传说保底的次数
# %lbp_<名称>.legBonusCount%  # 触发传说保底的总次数
# %lbp_<名称>.epicBonus%      # 未触发史诗保底的次数
# %lbp_<名称>.epicBonusCount% # 触发史诗保底的总次数
# %lbp_<名称>.legendary%      # 抽到传说的总次数
# %lbp_<名称>.epic%           # 抽到史诗的总次数
# %lbp_<名称>.normal%         # 抽到普通的总次数
# 占位符用法: %lbp_普通幸运方块.xxxx%
#

luckyBlock:
  # 方块名称(Bukkit名称)
  block: GRASS0
  # 概率(传说，史诗，普通)
  # 注意: 概率是无符号整数，三个概率相加不建议超过100.00(自行测试)。
  legendaryProbability: 5.00
  epicProbability: 5.00
  normalProbability: 90.00

  #
  # 传说保底机制
  # 介绍: 玩家开启幸运方块63次开不出传说宝可梦就在第64次必出传说宝可梦！
  #      请务必将 count: 64 参数设置为无符号整数，否则将会无限触发保底机制。
  #
  legBonus:
    # 是否开启该功能
    enable: true
    # 是否为异色
    isShiny: false
    # 触发保底的次数(无符号整数)
    count: 64
    # 执行的命令
    commands:
      - "console: Hello"
      # 支持参数(支持颜色字符，支持Papi):
      # - "console: 命令"   # 以控制台身份执行命令!
      # - "command: 命令 "  # 以玩家身份执行命令！
      # - "op: 命令"        # 以OP身份执行命令!
      # - "tell: 信息"      # 给玩家在聊天栏发送信息
      # - "actionbar: 你好" # 给玩家发送物品栏上方信息
      # - "title: `你好` `副标题` 10 100 10" # 给玩家发送屏幕标题
      #
      # - "consoleAll: 命令"   # 全部玩家以控制台身份执行命令!
      # - "commandAll: 命令 "  # 全部玩家以玩家身份执行命令！
      # - "opAll: 命令"        # 全部玩家以OP身份执行命令!
      # - "tellAll: 信息"      # 给全部玩家在聊天栏发送信息
      # - "actionbarAll: 你好" # 给全部玩家发送物品栏上方信息
      # - "titleAll: `你好` `副标题` 10 100 10" # 给全部玩家显示屏幕标题
      # 给全体在线玩家执行的命令玩家名称使用 {lbp_allPlayer} 代替，如果写玩家占位符依旧只会给一个玩家执行。
    # 生成的宝可梦
    pokemon:
      - "Pikachu"

  #
  # 史诗保底机制
  # 介绍: 玩家开启幸运方块31次开不出史诗宝可梦就在第32次必出史诗宝可梦！
  #      请务必将 count: 32 参数设置为无符号整数，否则将会无限触发保底机制。
  #
  epicBonus:
    enable: true
    isShiny: false
    count: 32
    commands:
      - "console: Hello"
    pokemon:
      - "Pikachu"

  #
  # 传说奖品
  # 幸运方块开出传说后都给予什么奖品？
  #
  legendary:
    enable: true
    isShiny: false
    commands:
      - "console: Hello"
    pokemon:
      - "Pikachu"

  #
  # 史诗奖品
  # 幸运方块开出史诗后都给予什么奖品？
  #
  epic:
    enable: true
    isShiny: false
    commands:
      - "console: Hello"
    pokemon:
      - "Pikachu"

  #
  # 普通奖品
  # 幸运方块开出普通后都给予什么奖品？
  #
  normal:
    isShiny: false
    commands:
      - "console: Hello"
    pokemon:
      - "Pikachu" 
```