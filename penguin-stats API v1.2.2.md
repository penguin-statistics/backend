# penguin-stats API

 <strong>version: 1.2.2</strong>

contributor：yamika

## Report 

### description:

物品掉落上传接口，汇报关卡物品掉落

- 使用用户ID和用户IP，从cookie中读取userID判断用户身份
- 根据上传来源分为内部上传和普通上传，对上传的掉落数据采取不同的判断策略
- ~

### URI

```
POST /PenguinStats/api/report
```

### 请求消息

#### 请求参数

| 参数         | 参数类型   | 是否必须 | 描述                                                         |
| ------------ | ---------- | -------- | ------------------------------------------------------------ |
| stageId      | String     | 是       | 关卡id                                                       |
| drops        | List(dict) | 是       | 物品掉落信息                                                 |
| furnitureNum | int        | 是       | 掉落家具数量，有限制，取值为1或0                             |
| version      | String     | 否       | API版本                                                      |
| source       | String     | 否       | 掉落数据来源，可用于区分是否来自内部上传,详见source[取值示例](#source取值示例) |

**drops参数信息**

| 参数     | 参数类型 | 是否必须 | 描述                                                |
| -------- | -------- | -------- | --------------------------------------------------- |
| itemId   | String   | 是       | 物品id                                              |
| quantity | int      | 是       | 掉落物品数量，遵循[limitation接口](#Limitation)规定 |

### 实例

- 请求示例

```json
{
 "stageId":"main_04-07",
 "furnitureNum":0,
 "drops":
    [
        {
   		"itemId":"30012",
        "quantity":1
        }
    ],
"source":"penguin-stats.io(internal)",
"version":"v1.2.2"
}
```

### 返回值

请参考[HTTP通用状态码](https://httpstatuses.com/)

## Result-Matrix

### description:

查询所有物品的掉落情况接口

- 对外部开放接口（企鹅数据API）
- 返回所有物品的掉落
- 每条记录对应不同物品在不同关卡掉落情况（掉落数/样本数）
- ~

### URI

```
GET /PenguinStats/api/result/matrix
```

### 响应消息

#### 响应参数

| 参数   | 参数类型   | 描述                                   |
| ------ | ---------- | -------------------------------------- |
| matrix | List(Dict) | 存储了所有物品在不同关卡掉落情况的矩阵 |

**matrix参数信息**

| 参数     | 参数类型 | 是否必选 | 描述           |
| -------- | -------- | -------- | -------------- |
| itemId   | String   | 是       | 掉落物品id     |
| times    | int      | 是       | 样本总数       |
| quantity | int      | 是       | 物品掉落的次数 |
| stageId  | String   | 是       | 关卡id         |

### 实例

- 请求实例

  ```
  GET /PenguinStats/api/result/matrix
  ```

- 返回实例

  ```
  {
      "matrix": [
          {
              "itemId": "30011",
              "times": 8,
              "quantity": 1,
              "stageId": "main_01-01"
          },
          {
              "itemId": "2001",
              "times": 6,
              "quantity": 7,
              "stageId": "main_01-01"
          },
          {
              "itemId": "30051",
              "times": 8,
              "quantity": 1,
              "stageId": "main_01-01"
          },
          {
              "itemId": "30031",
              "times": 41,
              "quantity": 2,
              "stageId": "main_01-05"
          },
          {
              "itemId": "30011",
              "times": 41,
              "quantity": 1,
              "stageId": "main_01-05"
          },
        ]
      }
  ```



### 返回值

请参考[HTTP通用状态码](https://httpstatuses.com/)

## Result-stage

### description:

关卡物品掉落结果（掉落率）查询接口，查询指定关卡不同物品掉落情况

- 根据请求方法和参数返回全平台掉落数据和个人掉落数据（目前个人掉落查询维护中）
- 目前个人掉落数据同时存储再浏览器LocalStorage中
- ~

### URI

```
GET /PenguinStats/api/result/stage/{stageId}
POST /PenguinStats/api/result/stage/{stageId}
```

参数说明

| 参数    | 类型   | 是否必须 | 描述   |
| ------- | ------ | -------- | ------ |
| stageId | String | 是       | 关卡id |

### 请求消息

说明：只针对POST请求

#### 请求参数

| 参数       | 类型       | 是否必须 | 描述                                           |
| ---------- | ---------- | -------- | ---------------------------------------------- |
| dropMatrix | List(Dict) | 是       | 掉落信息矩阵，存储了当前用户上传的所有掉落信息 |
| stageTimes | List(Dict) | 是       | 关卡样本数，存储了用户每个关卡上传的总次数     |

### 实例

1. GET

   - 请求示例

     ```
     GET /PenguinStats/api/result/stage/main_04-07
     ```

   - 返回实例

     ```json
     {
         "drops": [
             {
                 "times": 7075,
                 "item": {
                     "itemId": "30013",
                     "itemType": "MATERIAL",
                     "sortId": 35,
                     "name": "固源岩组",
                     "iconUrl": "https://s3.ap-southeast-1.amazonaws.com/penguin-stats-item-image/30013.png",
                     "rarity": 2
                 },
                 "quantity": 134
             },
             {
                 "times": 7075,
                 "item": {
                     "itemId": "30012",
                     "itemType": "MATERIAL",
                     "sortId": 36,
                     "name": "固源岩",
                     "iconUrl": "https://s3.ap-southeast-1.amazonaws.com/penguin-stats-item-image/30012.png",
                     "rarity": 1
                 },
                 "quantity": 2227
             },
             {
                 "times": 7075,
                 "item": {
                     "itemId": "30083",
                     "itemType": "MATERIAL",
                     "sortId": 29,
                     "name": "轻锰矿",
                     "iconUrl": "https://s3.ap-southeast-1.amazonaws.com/penguin-stats-item-image/30083.png",
                     "rarity": 2
                 },
                 "quantity": 2076
             }
         ],
         "stage": {
             "specialDrop": [
                 "30084"
             ],
             "extraDrop": [
                 "30012",
                 "30062",
                 "30013"
             ],
             "code": "4-7",
             "stageType": "MAIN",
             "normalDrop": [
                 "30083"
             ],
             "zoneId": "main_4",
             "apCost": 18,
             "stageId": "main_04-07"
         }
     }
     
     ```

2. POST

   - 请求示例

     ```json
     {"stageTimes":
      {
          "main_04-10":[51,51,51],
          "main_03-02":[20,20,20],
          "main_04-09":[17,17,17],
          "sub_03-1-1":[4,4,4],
          "main_00-07":[1,1,1],
          "main_01-03":[1,1,1],
          "main_04-07":[16,16,16],
          "main_04-08":[1,1,1],
          "main_01-07":[1,1,1],
          "main_05-01":[7,7,7],
          "main_05-02":[1,1,1],
          "main_05-03":[1,1,1]},
      "dropMatrix":
      {
          "main_04-10":{"3003":5,"30011":18,"30012":14,"30013":1,"30061":2,"30062":7,"30063":15,"30064":1,"30073":2,"furni":2},
          "main_03-02":{"30011":8,"30012":4,"30031":6,"30032":2,"30083":7},
          "main_04-09":{"30031":5,"30032":3,"30051":6,"30052":2,"30053":1,"30103":5,"30104":2,"furni":1},
          "sub_03-1-1":{"2002":1,"30021":1,"30022":6,"30041":1},
          "main_00-07":{"2001":1,"30021":1},
          "main_01-03":{"2001":1,"30041":1},
          "main_04-07":{"3003":3,"30011":5,"30012":2,"30013":1,"30062":5,"30083":4,"30084":1},
          "main_04-08":{"30031":1},
          "main_01-07":{"2001":1,"30012":1,"30061":1},
          "main_05-01":{"3003":2,"30011":2,"30012":1,"30013":4,"30061":2},
          "main_05-02":{"30062":1},
          "main_05-03":{"30022":2}}}
     ```

   - 返回实例

     ```
     {
         "drops": [
             {
                 "times": 16,
                 "item": {
                     "itemId": "30013",
                     "itemType": "MATERIAL",
                     "sortId": 35,
                     "name": "固源岩组",
                     "iconUrl": "https://s3.ap-southeast-1.amazonaws.com/penguin-stats-item-image/30013.png",
                     "rarity": 2
                 },
                 "quantity": 1
             },
             {
                 "times": 16,
                 "item": {
                     "itemId": "30012",
                     "itemType": "MATERIAL",
                     "sortId": 36,
                     "name": "固源岩",
                     "iconUrl": "https://s3.ap-southeast-1.amazonaws.com/penguin-stats-item-image/30012.png",
                     "rarity": 1
                 },
                 "quantity": 2
             },
             {
                 "times": 16,
                 "item": {
                     "itemId": "30083",
                     "itemType": "MATERIAL",
                     "sortId": 29,
                     "name": "轻锰矿",
                     "iconUrl": "https://s3.ap-southeast-1.amazonaws.com/penguin-stats-item-image/30083.png",
                     "rarity": 2
                 },
                 "quantity": 4
             }
         ],
         "stage": {
             "specialDrop": [
                 "30084"
             ],
             "extraDrop": [
                 "30012",
                 "30062",
                 "30013",
                 "30063",
                 "30073",
                 "3003"
             ],
             "code": "4-7",
             "stageType": "MAIN",
             "normalDrop": [
                 "30083"
             ],
             "zoneId": "main_4",
             "apCost": 18,
             "stageId": "main_04-07"
         }
     }
     
     ```

### 返回值

请参考[HTTP通用状态码](https://httpstatuses.com/)

## Result-Item

### description:

指定物品掉落查询接口，查询指定物品在不同关卡掉落情况

- 根据请求方法和参数返回全平台掉落数据和个人掉落数据（目前个人掉落查询维护中）
- 目前个人掉落数据同时存储再浏览器LocalStorage中
- ~

### URI

```
GET /PenguinStats/api/result/item/{ItemId}
POST /PenguinStats/api/result/item/{ItemId}
```

参数说明

| 参数   | 类型   | 是否必须 | 描述   |
| ------ | ------ | -------- | ------ |
| ItemId | String | 是       | 物品id |

### 请求消息

说明：只针对POST请求

#### 请求参数

| 参数       | 类型       | 是否必须 | 描述                                           |
| ---------- | ---------- | -------- | ---------------------------------------------- |
| dropMatrix | List(Dict) | 是       | 掉落信息矩阵，存储了当前用户上传的所有掉落信息 |
| stageTimes | List(Dict) | 是       | 关卡样本数，存储了用户每个关卡上传的总次数     |

### 实例

1. GET

   - 请求示例

     ```
     GET /PenguinStats/api/result/item/30034
     ```

   - 返回实例

     ```
     {
         "item": {
             "itemId": "30034",
             "itemType": "MATERIAL",
             "sortId": 42,
             "name": "聚酸酯块",
             "iconUrl": "https://s3.ap-southeast-1.amazonaws.com/penguin-stats-item-image/30034.png",
             "rarity": 3
         },
         "drops": [
             {
                 "times": 2751,
                 "quantity": 91,
                 "stage": {
                     "specialDrop": [
                         "30034"
                     ],
                     "extraDrop": [
                         "30031",
                         "30051",
                         "30032",
                         "30052",
                         "30033",
                         "30053",
                         "30093",
                         "30103"
                     ],
                     "code": "3-8",
                     "stageType": "MAIN",
                     "normalDrop": [
                         "30033"
                     ],
                     "zoneId": "main_3",
                     "apCost": 18,
                     "stageId": "main_03-08"
                 }
             }
         ]
     }
     ```

2. POST

   - 请求示例

     ```
     {"stageTimes":
      {
          "main_04-10":[51,51,51],
          "main_03-02":[20,20,20],
          "main_04-09":[17,17,17],
          "sub_03-1-1":[4,4,4],
          "main_00-07":[1,1,1],
          "main_01-03":[1,1,1],
          "main_04-07":[16,16,16],
          "main_04-08":[1,1,1],
          "main_01-07":[1,1,1],
          "main_05-01":[7,7,7],
          "main_05-02":[1,1,1],
          "main_05-03":[1,1,1]},
      "dropMatrix":
      {
          "main_04-10":{"3003":5,"30011":18,"30012":14,"30013":1,"30061":2,"30062":7,"30063":15,"30064":1,"30073":2,"furni":2},
          "main_03-02":{"30011":8,"30012":4,"30031":6,"30032":2,"30083":7},
          "main_04-09":{"30031":5,"30032":3,"30051":6,"30052":2,"30053":1,"30103":5,"30104":2,"furni":1},
          "sub_03-1-1":{"2002":1,"30021":1,"30022":6,"30041":1},
          "main_00-07":{"2001":1,"30021":1},
          "main_01-03":{"2001":1,"30041":1},
          "main_04-07":{"3003":3,"30011":5,"30012":2,"30013":1,"30062":5,"30083":4,"30084":1},
          "main_04-08":{"30031":1},
          "main_01-07":{"2001":1,"30012":1,"30061":1},
          "main_05-01":{"3003":2,"30011":2,"30012":1,"30013":4,"30061":2},
          "main_05-02":{"30062":1},
          "main_05-03":{"30022":2}}}
     ```

   - 返回示例

     ```
     {
         "item": {
             "itemId": "30034",
             "itemType": "MATERIAL",
             "sortId": 42,
             "name": "聚酸酯块",
             "iconUrl": "https://s3.ap-southeast-1.amazonaws.com/penguin-stats-item-image/30034.png",
             "rarity": 3
         },
         "drops": [
         ]
     }
     ```

### 返回值

请参考[HTTP通用状态码](https://httpstatuses.com/)

## Zone

### description:

查询所有作战章节信息，返回所有作战章节包含的关卡

- 返回内容只包括关卡Id和章节名称
- ~

### URI

```
GET /PenguinStats/api/result/zone
```

### 响应消息

#### 响应参数

| 参数  | 参数类型   | 描述                 |
| ----- | ---------- | -------------------- |
| zones | List(Dict) | 存储了章节信息的列表 |

**zones参数信息**

| 参数      | 参数类型     | 是否必选 | 描述                                                         |
| --------- | ------------ | -------- | ------------------------------------------------------------ |
| zoneIndex | int          | 是       | 章节索引（序号）                                             |
| stages    | List(String) | 是       | 样本总数                                                     |
| zoneId    | String       | 是       | 章节id                                                       |
| zoneName  | String       | 是       | 章节名称                                                     |
| type      | String       | 是       | 章节类型，取值见[type取值说明](#type取值说明)                |
| closeTime | long         | 否       | 活动章节关闭时间，只当type为ACTIVITY时会有，格式为unix时间戳 |
| openTime  | long         | 否       | 活动章节开启时间，只当type为ACTIVITY时会有，格式为unix时间戳 |

### 实例

### 实例

- 请求示例

  ```
  GET /PenguinStats/api/result/zone
  ```

- 返回实例

  ```
  {
      "zones": [
          {
              "zoneIndex": 0,
              "stages": [
                  "main_00-01",
                  "main_00-02",
                  "main_00-03",
                  "main_00-04",
                  "main_00-05",
                  "main_00-06",
                  "main_00-07",
                  "main_00-08",
                  "main_00-09",
                  "main_00-10",
                  "main_00-11"
              ],
              "zoneId": "main_0",
              "zoneName": "序章",
              "type": "MAINLINE"
          },
          {
              "zoneIndex": 1,
              "stages": [
                  "main_01-01",
                  "main_01-02",
                  "main_01-03",
                  "main_01-04",
                  "main_01-05",
                  "main_01-06",
                  "main_01-07",
                  "main_01-08",
                  "main_01-09",
                  "main_01-10",
                  "main_01-11",
                  "main_01-12"
              ],
              "zoneId": "main_1",
              "zoneName": "第一章",
              "type": "MAINLINE"
          },
          {
              "zoneIndex": 2,
              "stages": [
                  "main_02-01",
                  "main_02-02",
                  "main_02-03",
                  "main_02-04",
                  "main_02-05",
                  "main_02-06",
                  "main_02-07",
                  "main_02-08",
                  "main_02-09",
                  "main_02-10",
                  "sub_02-01",
                  "sub_02-02",
                  "sub_02-03",
                  "sub_02-04",
                  "sub_02-05",
                  "sub_02-06",
                  "sub_02-07",
                  "sub_02-08",
                  "sub_02-09",
                  "sub_02-10",
                  "sub_02-11",
                  "sub_02-12"
              ],
              "zoneId": "main_2",
              "zoneName": "第二章",
              "type": "MAINLINE"
          }
  }
  ```

### 返回值

请参考[HTTP通用状态码](https://httpstatuses.com/)

## Zone-stage（已过时，不推荐使用）

父接口：[Zone](#Zone)

### description:

查询指定章节关卡掉落接口

- 返回该章节所有关卡信息，只包含关卡掉落物品种类不包含具体掉落情况
- **推荐使用父接口**
- ~

### URI

```
GET /PenguinStats/api/result/zone/{zoneId}/stage
```

参数说明

| 参数   | 类型   | 是否必须 | 描述   |
| ------ | ------ | -------- | ------ |
| zoneId | String | 是       | 章节id |

### 响应消息

#### 响应参数

| 参数   | 参数类型   | 描述                                   |
| ------ | ---------- | -------------------------------------- |
| stages | List(Dict) | 存储了所查询章节所有关卡掉落信息的列表 |

**stages参数信息**

| 参数        | 参数类型     | 是否必选 | 描述                                          |
| ----------- | ------------ | -------- | --------------------------------------------- |
| specialDrop | List(String) | 是       | 特殊掉落物id集合                              |
| extraDrop   | List(String) | 是       | 额外掉落物id集合                              |
| code        | String       | 是       | 关卡编号                                      |
| stageType   | String       | 是       | 关卡类型，取值见[type取值说明](#type取值说明) |
| normalDrop  | List(String) | 是       | 通常掉落物id集合                              |
| zoneId      | String       | 是       | 章节id                                        |
| dropsSet    | List(String) | 是       | 掉落物id集合                                  |
| apCost      | int          | 是       | 关卡理智消耗                                  |
| stageId     | String       | 是       | 关卡id                                        |

### 实例

- 请求示例

  ```
  GET /PenguinStats/api/zone/main_0/stage
  ```

- 返回实例

  ```
  {
      "stages": [
          {
              "specialDrop": [
              ],
              "extraDrop": [
                  "30011",
                  "30021",
                  "30031",
                  "30041",
                  "30051",
                  "30061",
                  "3003",
                  "2001"
              ],
              "code": "0-1",
              "stageType": "MAIN",
              "normalDrop": [
                  "2001"
              ],
              "zoneId": "main_0",
              "dropsSet": [
                  "30031",
                  "30041",
                  "30011",
                  "30021",
                  "3003",
                  "2001",
                  "30051",
                  "furni",
                  "30061"
              ],
              "apCost": 6,
              "stageId": "main_00-01"
          },
          {
              "specialDrop": [
              ],
              "extraDrop": [
                  "30011",
                  "30021",
                  "30031",
                  "30041",
                  "30051",
                  "30061",
                  "3003",
                  "2001"
              ],
              "code": "0-2",
              "stageType": "MAIN",
              "normalDrop": [
                  "2001"
              ],
              "zoneId": "main_0",
              "dropsSet": [
                  "30031",
                  "30041",
                  "30011",
                  "30021",
                  "3003",
                  "2001",
                  "30051",
                  "furni",
                  "30061"
              ],
              "apCost": 6,
              "stageId": "main_00-02"
          },
          {
              "specialDrop": [
              ],
              "extraDrop": [
                  "30011",
                  "30021",
                  "30031",
                  "30041",
                  "30051",
                  "30061",
                  "3003",
                  "2001"
              ],
              "code": "0-3",
              "stageType": "MAIN",
              "normalDrop": [
                  "2001"
              ],
              "zoneId": "main_0",
              "dropsSet": [
                  "30031",
                  "30041",
                  "30011",
                  "30021",
                  "3003",
                  "2001",
                  "30051",
                  "furni",
                  "30061"
              ],
              "apCost": 6,
              "stageId": "main_00-03"
          },
          
      ]
  }
  
  ```

### 返回值

请参考[HTTP通用状态码](https://httpstatuses.com/)

## Stage

类似接口： [Zone](#Zone)

### description:

查询所有关卡物品掉落种类

- 对外部开放接口（企鹅数据API）
- 不包含关卡所属章节信息
- 不包含关卡掉落详情

### URI

```
GET /PenguinStats/api/stage
```

### 响应消息

#### 响应参数

| 参数   | 参数类型   | 描述                               |
| ------ | ---------- | ---------------------------------- |
| stages | List(Dict) | 存储了所有章节的关卡掉落信息的列表 |

**stages参数信息**

| 参数        | 参数类型     | 是否必选 | 描述                                          |
| ----------- | ------------ | -------- | --------------------------------------------- |
| specialDrop | List(String) | 是       | 特殊掉落物id集合                              |
| extraDrop   | List(String) | 是       | 额外掉落物id集合                              |
| code        | String       | 是       | 关卡编号                                      |
| stageType   | String       | 是       | 关卡类型，取值见[type取值说明](#type取值说明) |
| normalDrop  | List(String) | 是       | 通常掉落物id集合                              |
| zoneId      | String       | 是       | 章节id                                        |
| dropsSet    | List(String) | 是       | 掉落物id集合                                  |
| apCost      | int          | 是       | 关卡理智消耗                                  |
| stageId     | String       | 是       | 关卡id         |

### 实例

- 请求示例

  ```
  GET /PenguinStats/api/stage
  ```

- 返回实例

  ```
   {
      "stages": [
          {
              "specialDrop": [
              ],
              "extraDrop": [
                  "30011",
                  "30021",
                  "30031",
                  "30041",
                  "30051",
                  "30061",
                  "3003",
                  "2001"
              ],
              "code": "1-1",
              "stageType": "MAIN",
              "normalDrop": [
              ],
              "zoneId": "main_1",
              "apCost": 6,
              "stageId": "main_01-01"
          },
          {
              "specialDrop": [
              ],
              "extraDrop": [
                  "30011",
                  "30021",
                  "30031",
                  "30041",
                  "30051",
                  "30061",
                  "3003",
                  "2001"
              ],
              "code": "1-5",
              "stageType": "MAIN",
              "normalDrop": [
                  "30061"
              ],
              "zoneId": "main_1",
              "apCost": 6,
              "stageId": "main_01-05"
          },
          {
              "specialDrop": [
              ],
              "extraDrop": [
                  "30011",
                  "30021",
                  "30031",
                  "30041",
                  "30051",
                  "30061",
                  "3003",
                  "2001"
              ],
              "code": "1-4",
              "stageType": "MAIN",
              "normalDrop": [
              ],
              "zoneId": "main_1",
              "apCost": 6,
              "stageId": "main_01-04"
          },
      ]
  }
  ```

### 返回值

请参考[HTTP通用状态码](https://httpstatuses.com/)

## Stage-id（已过时，不推荐使用）

父接口：[Stage](#Stage)

### description:

查询指定关卡物品掉落种类

- 对外部开放接口（企鹅数据API）
- 不包含关卡所属章节信息
- 不包含关卡掉落详情
- **推荐使用父接口**
- ~

### URI

```
GET /PenguinStats/api/stage/{stageId}
```

参数说明

| 参数    | 类型   | 是否必须 | 描述   |
| ------- | ------ | -------- | ------ |
| stageId | String | 是       | 关卡id |

### 响应消息

#### 响应参数

| 参数        | 参数类型     | 描述                                          |
| ----------- | ------------ | --------------------------------------------- |
| specialDrop | List(Dict)   | 特殊掉落物集合                                |
| extraDrop   | List(Dict)   | 额外掉落物集合                                |
| code        | String       | 关卡编号                                      |
| stageType   | String       | 关卡类型，取值见[type取值说明](#type取值说明) |
| normalDrop  | List(Dict)   | 通常掉落物集合                                |
| zoneId      | String       | 章节id                                        |
| dropsSet    | List(String) | 掉落物id集合                                  |
| apCost      | int          | 关卡理智消耗                                  |
| stageId     | String       | 关卡id                                        |

**specialDrop,extraDrop,normalDrop参数信息**

| 参数     | 参数类型 | 是否必选 | 描述                                                    |
| -------- | -------- | -------- | ------------------------------------------------------- |
| itemId   | String   | 是       | 掉落物id                                                |
| itemType | String   | 是       | 掉落物类型，取值见[itemType取值说明](#itemType取值说明) |
| sortId   | int      | 是       | 掉落物排序id                                            |
| name     | String   | 是       | 掉落物名称                                              |
| iconUrl  | url      | 是       | 掉落物icon uri                                          |
| rarity   | int      | 是       | 掉落物稀有度                                            |



### 实例

- 请求示例

  ```
  GET /PenguinStats/api/stage/main_04-06
  ```

- 返回实例

  ```
  {
      "specialDrop": [
          {
              "itemId": "30014",
              "itemType": "MATERIAL",
              "sortId": 34,
              "name": "提纯源岩",
              "iconUrl": "https://s3.ap-southeast-1.amazonaws.com/penguin-stats-item-image/30014.png",
              "rarity": 3
          }
      ],
      "extraDrop": [
          {
              "itemId": "30011",
              "itemType": "MATERIAL",
              "sortId": 37,
              "name": "源岩",
              "iconUrl": "https://s3.ap-southeast-1.amazonaws.com/penguin-stats-item-image/30011.png",
              "rarity": 0
          },
          {
              "itemId": "30061",
              "itemType": "MATERIAL",
              "sortId": 41,
              "name": "破损装置",
              "iconUrl": "https://s3.ap-southeast-1.amazonaws.com/penguin-stats-item-image/30061.png",
              "rarity": 0
          },
          {
              "itemId": "30012",
              "itemType": "MATERIAL",
              "sortId": 36,
              "name": "固源岩",
              "iconUrl": "https://s3.ap-southeast-1.amazonaws.com/penguin-stats-item-image/30012.png",
              "rarity": 1
          },
          {
              "itemId": "30062",
              "itemType": "MATERIAL",
              "sortId": 40,
              "name": "装置",
              "iconUrl": "https://s3.ap-southeast-1.amazonaws.com/penguin-stats-item-image/30062.png",
              "rarity": 1
          },
          {
              "itemId": "30013",
              "itemType": "MATERIAL",
              "sortId": 35,
              "name": "固源岩组",
              "iconUrl": "https://s3.ap-southeast-1.amazonaws.com/penguin-stats-item-image/30013.png",
              "rarity": 2
          },
          {
              "itemId": "30063",
              "itemType": "MATERIAL",
              "sortId": 39,
              "name": "全新装置",
              "iconUrl": "https://s3.ap-southeast-1.amazonaws.com/penguin-stats-item-image/30063.png",
              "rarity": 2
          },
          {
              "itemId": "30073",
              "itemType": "MATERIAL",
              "sortId": 27,
              "name": "扭转醇",
              "iconUrl": "https://s3.ap-southeast-1.amazonaws.com/penguin-stats-item-image/30073.png",
              "rarity": 2
          },
          {
              "itemId": "3003",
              "itemType": "MATERIAL",
              "addTime": 1,
              "sortId": 20,
              "name": "赤金",
              "iconUrl": "https://s3.ap-southeast-1.amazonaws.com/penguin-stats-item-image/3003.png",
              "rarity": 3
          }
      ],
      "code": "4-6",
      "stageType": "MAIN",
      "normalDrop": [
          {
              "itemId": "30013",
              "itemType": "MATERIAL",
              "sortId": 35,
              "name": "固源岩组",
              "iconUrl": "https://s3.ap-southeast-1.amazonaws.com/penguin-stats-item-image/30013.png",
              "rarity": 2
          }
      ],
      "zoneId": "main_4",
      "apCost": 18,
      "stageId": "main_04-06"
  }
  
  ```

### 返回值

请参考[HTTP通用状态码](https://httpstatuses.com/)

## Item

### description:

物品查询接口

- 对外部开放接口（企鹅数据API）
- 目前只支持查询所有物品信息，不支持指定物品信息查询
- ~

### URI

```
GET  PenguinStats/api/items
```

### 响应消息

#### 响应参数

| 参数  | 参数类型   | 描述                               |
| ----- | ---------- | ---------------------------------- |
| items | List(Dict) | 存储了所有章节的关卡掉落信息的列表 |

**items参数信息**

| 参数     | 参数类型 | 是否必选 | 描述                                                    |
| -------- | -------- | -------- | ------------------------------------------------------- |
| itemId   | String   | 是       | 掉落物id                                                |
| itemType | String   | 是       | 掉落物类型，取值见[itemType取值说明](#itemType取值说明) |
| sortId   | int      | 是       |                                                         |
| name     | String   | 是       | 掉落物名称                                              |
| iconUrl  | url      | 是       | 掉落物icon uri                                          |
| rarity   | int      | 是       | 掉落物稀有度                                            |

### 实例

- 请求示例

  ```
  GET  PenguinStats/api/items
  ```

- 返回示例

  ```
  {
      "items": [
          {
              "itemId": "2004",
              "itemType": "CARD_EXP",
              "addTime": 1,
              "sortId": 12,
              "name": "高级作战记录",
              "iconUrl": "https://s3.ap-southeast-1.amazonaws.com/penguin-stats-item-image/2004.png",
              "rarity": 4
          },
          {
              "itemId": "2003",
              "itemType": "CARD_EXP",
              "addTime": 1,
              "sortId": 13,
              "name": "中级作战记录",
              "iconUrl": "https://s3.ap-southeast-1.amazonaws.com/penguin-stats-item-image/2003.png",
              "rarity": 3
          },
          {
              "itemId": "2002",
              "itemType": "CARD_EXP",
              "addTime": 1,
              "sortId": 14,
              "name": "初级作战记录",
              "iconUrl": "https://s3.ap-southeast-1.amazonaws.com/penguin-stats-item-image/2002.png",
              "rarity": 2
          },
          {
              "itemId": "2001",
              "itemType": "CARD_EXP",
              "addTime": 1,
              "sortId": 15,
              "name": "基础作战记录",
              "iconUrl": "https://s3.ap-southeast-1.amazonaws.com/penguin-stats-item-image/2001.png",
              "rarity": 1
          },
       ]
  
  ```

### 返回值

请参考[HTTP通用状态码](https://httpstatuses.com/)

## Limitation

### description:

查询关卡物品掉落数量上下限接口

- 目前包含掉落为0可能
- 目前只提供查询所有关卡物品掉落数量限制，不提供指定关卡或物品数量限制查询
- ~

### URI

```
GET /PenguinStats/api/limitation
```

### 响应消息

#### 响应参数

| 参数        | 参数类型   | 描述                   |
| ----------- | ---------- | ---------------------- |
| limitations | List(Dict) | 掉落物掉落限制信息集合 |

**limitations参数信息**

| 参数               | 参数类型   | 是否必选 | 描述                         |
| ------------------ | ---------- | -------- | ---------------------------- |
| itemQuantityBounds | List(Dict) | 是       | 掉落物掉落数量上下限         |
| name               | String     | 是       | 关卡id                       |
| itemTypeBounds     | List(Dict) | 是       | 关卡可掉落物类型的数量上下限 |

**itemQuantityBounds参数信息**

| 参数   | 参数类型   | 是否必选 | 描述                 |
| ------ | ---------- | -------- | -------------------- |
| bounds | List(Dict) | 是       | 掉落物掉落数量上下限 |
| itemId | String     | 是       | 掉落物id             |

**itemTypeBounds参数信息**

| 参数   | 参数类型   | 是否必选 | 描述                         |
| ------ | ---------- | -------- | ---------------------------- |
| bounds | List(Dict) | 是       | 关卡可掉落物类型的数量上下限 |

**bounds参数信息**

| 参数  | 参数类型 | 是否必选 | 描述     |
| ----- | -------- | -------- | -------- |
| lower | int      | 是       | 数量下限 |
| uper  | int      | 是       | 数量上限 |

### 实例

- 请求示例

  ```
  GET /PenguinStats/api/limitation
  ```

- 返回示例

  ```json
  {
      "limitations": [
          {
              "itemQuantityBounds": [
                  {
                      "itemId": "30061",
                      "bounds": {
                          "lower": 0,
                          "upper": 1
                      }
                  },
                  {
                      "itemId": "30041",
                      "bounds": {
                          "lower": 0,
                          "upper": 1
                      }
                  },
                  {
                      "itemId": "30021",
                      "bounds": {
                          "lower": 0,
                          "upper": 1
                      }
                  },
                  {
                      "itemId": "2001",
                      "bounds": {
                          "lower": 0,
                          "upper": 3
                      }
                  },
                  {
                      "itemId": "30051",
                      "bounds": {
                          "lower": 0,
                          "upper": 1
                      }
                  },
                  {
                      "itemId": "furni",
                      "bounds": {
                          "lower": 0,
                          "upper": 1
                      }
                  },
                  {
                      "itemId": "30031",
                      "bounds": {
                          "lower": 0,
                          "upper": 1
                      }
                  },
                  {
                      "itemId": "30011",
                      "bounds": {
                          "lower": 0,
                          "upper": 1
                      }
                  },
                  {
                      "itemId": "3003",
                      "bounds": {
                          "lower": 0,
                          "upper": 1
                      }
                  }
              ],
              "name": "main_01-01",
              "itemTypeBounds": {
                  "lower": 1,
                  "upper": 2
              }
          },
          {
              "itemQuantityBounds": [
                  {
                      "itemId": "30061",
                      "bounds": {
                          "lower": 0,
                          "upper": 3
                      }
                  },
                  {
                      "itemId": "30041",
                      "bounds": {
                          "lower": 0,
                          "upper": 1
                      }
                  },
                  {
                      "itemId": "30021",
                      "bounds": {
                          "lower": 0,
                          "upper": 1
                      }
                  },
                  {
                      "itemId": "2001",
                      "bounds": {
                          "lower": 0,
                          "upper": 2
                      }
                  },
                  {
                      "itemId": "30051",
                      "bounds": {
                          "lower": 0,
                          "upper": 1
                      }
                  },
                  {
                      "itemId": "furni",
                      "bounds": {
                          "lower": 0,
                          "upper": 1
                      }
                  },
                  {
                      "itemId": "30031",
                      "bounds": {
                          "lower": 0,
                          "upper": 1
                      }
                  },
                  {
                      "itemId": "30011",
                      "bounds": {
                          "lower": 0,
                          "upper": 1
                      }
                  },
                  {
                      "itemId": "3003",
                      "bounds": {
                          "lower": 0,
                          "upper": 1
                      }
                  }
              ],
              "name": "main_01-05",
              "itemTypeBounds": {
                  "lower": 0,
                  "upper": 3
              }
          }
          ]
  }
  ```

### 返回值

请参考[HTTP通用状态码](https://httpstatuses.com/)

## User

### description:

用户功能接口

- 用户登录
- ~~普通用户注册~~
- 内部用户注册
- ~

### URI

```
POST /PenguinStats/api/users
```

### 请求消息

#### 请求参数

| 参数   | 类型   | 是否必须 | 描述   |
| ------ | ------ | -------- | ------ |
| userID | String | 是       | 用户id |

### 响应消息

#### 响应参数

| 参数       | 参数类型     | 描述                           |
| ---------- | ------------ | ------------------------------ |
| createTime | long         | 用户创建时间，格式为unix时间戳 |
| userID     | String       | 用户id                         |
| ips        | List(String) | 用户所使用过的ip集合           |
| tags       | List(String) | 用户标签集合                   |

### 实例

- 请求示例

  ```
  {userID:"15748941"}
  ```

- 返回实例

  ```
  {
      "createTime": 1563421216503,
      "userID": "15748941",
      "ips": [
          "0.0.0.0" //示例用ip
      ],
      "tags": [
      ]
  }
  
  ```

### 返回值

请参考[HTTP通用状态码](https://httpstatuses.com/)

## 附录

### source取值示例

| 取值                   | 描述                     |
| ---------------------- | ------------------------ |
| penguin-stats.io       | 企鹅数据网站用户上传     |
| arknights-drop-sniffer | 企鹅数据掉落自动上传脚本 |
| etc.                   |                          |

### type取值说明

| 取值                   | 描述          |
| ---------------------- | ------------- |
| MAINLINE               | 主线关卡/章节 |
| arknights-drop-sniffer | 日常关卡/章节 |
| ACTIVITY               | 活动关卡/章节 |

### itemType取值说明

| 取值     | 描述     |
| -------- | -------- |
| CARD_EXP | 经验录像 |
| MATERIAL | 材料     |
| FURN     | 家具     |



**文档内部分返回实例由于篇幅原因有删减**
