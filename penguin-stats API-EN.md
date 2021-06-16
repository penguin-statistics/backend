# penguin-stats API

 <strong>version: 1.2.2</strong>

contributor：yamika

[中文版](./penguin-stats API-CN.md)

## Report 

### description:

An interface for report of dropped items.

- using User ID and User IP, fetch userID from cookie to judge user status
- using different judge strategies by different report sources (internal upload & normal upload)

### URI

```
POST /PenguinStats/api/report
```

### Request Message

**request Parameters**

| Parameters   | Type       | Must  | Description                                                  |
| ------------ | ---------- | ----- | ------------------------------------------------------------ |
| stageId      | String     | True  | stage ID                                                     |
| drops        | List(dict) | True  | what dropped in this stage                                   |
| furnitureNum | int        | True  | number of dropped furniture, only 1 or 0 is acceptable.      |
| version      | String     | False | API version                                                  |
| source       | String     | False | source of report, for notion of internal upload, restricted by [source values](#source values) |

**drops Parameters**

| Parameters | Type   | Must | Description                                                  |
| ---------- | ------ | ---- | ------------------------------------------------------------ |
| itemId     | String | True | item ID                                                      |
| quantity   | int    | True | number of dropped items, restricted by [Limitation](#Limitation) |

### Example

- request example

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

### return

Please refer to [HTTP Status Codes](https://httpstatuses.com/).

## Result-Matrix

### description:

An interface for reading dropping of all items at different stages.

- open to external usage (Penguin-Statistics API)
- return dropping of all items
- Each single line contains dropping of an item at different stages (dropped quantity / samples)
- ~

### URI

```
GET /PenguinStats/api/result/matrix
```

### Response Message

#### response Parameters

| Parameters | Type       | Description                                                  |
| ---------- | ---------- | ------------------------------------------------------------ |
| matrix     | List(Dict) | a matrix which stores dropping of all items at different stages. |

**matrix Parameters**

| Parameters | Type   | Must | Description               |
| ---------- | ------ | ---- | ------------------------- |
| itemId     | String | True | dropped item ID           |
| times      | int    | True | number of valid samples   |
| quantity   | int    | True | quantity of dropped items |
| stageId    | String | True | stage id                  |

### Examples

- request example

  ```
  GET /PenguinStats/api/result/matrix
  ```

- return example

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

### return

Please refer to [HTTP Status Codes](https://httpstatuses.com/).

## Result-stage

### description:

An interface for reading dropped results at specified stage. 

- return dropped results at all platforms and personal data (personal dropping data is currently under maintenance)
- currently personal dropping data is stored in browser LocalStorage
- ~

### URI

```
GET /PenguinStats/api/result/stage/{stageId}
POST /PenguinStats/api/result/stage/{stageId}
```

**Parameters**

| Parameters | Type   | Must | Description |
| ---------- | ------ | ---- | ----------- |
| stageId    | String | True | stage ID    |

### Request Message

Remark: only for POST request.

#### request Parameters

| Parameters | Type       | Must | Description                                                  |
| ---------- | ---------- | ---- | ------------------------------------------------------------ |
| dropMatrix | List(Dict) | True | a matrix which stores dropping of all items at different stages reported by **current user** |
| stageTimes | List(Dict) | True | number of samples at some stage, which contains total upload times at each stage by user |

### Examples

1. GET

   - request example

     ```http
     GET /PenguinStats/api/result/stage/main_04-07
     ```

   - return example

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

   - request example

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

   - return example

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

### return

Please refer to [HTTP Status Codes](https://httpstatuses.com/).

## Result-Item

### description:

An interface for reading dropped results at specified item. 

- return dropped results at all platforms and personal data (personal dropping data is currently under maintenance)
- currently personal dropping data is stored in browser LocalStorage
- ~

### URI

```
GET /PenguinStats/api/result/item/{ItemId}
POST /PenguinStats/api/result/item/{ItemId}
```

**Parameters**

| Parameters | Type   | Must | Description |
| ---------- | ------ | ---- | ----------- |
| ItemId     | String | True | item ID     |

### Request Message

Remark: only for POST request.

#### request Parameters

| Parameters | Type       | Must | Description                                                  |
| ---------- | ---------- | ---- | ------------------------------------------------------------ |
| dropMatrix | List(Dict) | True | a matrix which stores dropping of all items at different stages reported by **current user** |
| stageTimes | List(Dict) | True | number of samples at some stage, which contains total upload times at each stage by user |

### Examples

1. GET

   - request example

     ```http
     GET /PenguinStats/api/result/item/30034
     ```

   - return example

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

   - request example

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

   - return example

     ```jso
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

### return

Please refer to [HTTP Status Codes](https://httpstatuses.com/).

## Zone

### description:

An interface for reading all battle chapters, return all stages contained in all chapters.

- return only contains stage id and chapter names
- ~

### URI

```http
GET /PenguinStats/api/result/zone
```

### Response Message

#### response Parameters

| Parameters | Type       | Description                     |
| ---------- | ---------- | ------------------------------- |
| zones      | List(Dict) | a list containing chapters info |

**Zones Parameters**

| Parameters | Type         | Must  | Description                                                  |
| ---------- | ------------ | ----- | ------------------------------------------------------------ |
| zoneIndex  | int          | True  | index of chapters (by number)                                |
| stages     | List(String) | True  | a list containing stage ids                                  |
| zoneId     | String       | True  | chapter id                                                   |
| zoneName   | String       | True  | chapter name                                                 |
| type       | String       | True  | chapter type, value are restricted by [type values](#type values) |
| closeTime  | long         | False | event chapter close time, only valid when type is ACTIVTY, format is UNIX timestamp |
| openTime   | long         | False | event chapter open time, only valid when type is ACTIVTY, format is UNIX timestamp |

### Examples

- request example

  ```http
  GET /PenguinStats/api/result/zone
  ```

- return example

  ```json
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

### return

Please refer to [HTTP Status Codes](https://httpstatuses.com/).

## Zone-stage (deprecated)

Father interface: [Zone](#Zone)

### description:

An interface for reading dropping at specified chapter.

* return all stages in this chapter, only containing dropping items but no dropping info
* **father interface is recommended**
* ~

### URI

```
GET /PenguinStats/api/result/zone/{zoneId}/stage
```

**Parameters**

| Parameters | Type   | Must | Description |
| ---------- | ------ | ---- | ----------- |
| zoneId     | String | True | chapter id  |

### Response Message

#### respone Parameters

| Parameters | Type       | Description                                                  |
| ---------- | ---------- | ------------------------------------------------------------ |
| stages     | List(Dict) | a list containing dropping info of all stages in specified chapter |

**stages Parameters**

| Parameters  | Type         | Must | Description                                           |
| ----------- | ------------ | ---- | ----------------------------------------------------- |
| specialDrop | List(String) | True | set of special drop items id                          |
| extraDrop   | List(String) | True | set of extra drop items id                            |
| code        | String       | True | stage code                                            |
| stageType   | String       | True | stage type, restricted by [type values](#type values) |
| normalDrop  | List(String) | True | set of common drop items id                           |
| zoneId      | String       | True | chapter id                                            |
| dropsSet    | List(String) | True | set of drop items id                                  |
| apCost      | int          | True | ap cost of stage                                      |
| stageId     | String       | True | stage id                                              |

### Examples

- request example

  ```http
  GET /PenguinStats/api/zone/main_0/stage
  ```

- return example

  ```json
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

### return

Please refer to [HTTP Status Codes](https://httpstatuses.com/).

## Stage

Similar interface: [Zone](#Zone)

### description:

An interface for reading dropping of all stages.

- open to external usage (Penguin-Statistics API)
- chapter of stages is not included
- dropping info of stages is not included

### URI

```
GET /PenguinStats/api/stage
```

### Response Message

#### response Parameters

| Parameters | Type       | Description                                     |
| ---------- | ---------- | ----------------------------------------------- |
| stages     | List(Dict) | a list containing dropping info of all chapters |

**stages Parameters**

| Parameters  | Type         | Must | Description                                           |
| ----------- | ------------ | ---- | ----------------------------------------------------- |
| specialDrop | List(String) | True | set of special drop items id                          |
| extraDrop   | List(String) | True | set of extra drop items id                            |
| code        | String       | True | stage code                                            |
| stageType   | String       | True | stage type, restricted by [type values](#type values) |
| normalDrop  | List(String) | True | set of common drop items id                           |
| zoneId      | String       | True | chapter id                                            |
| dropsSet    | List(String) | True | set of drop items id                                  |
| apCost      | int          | True | ap cost of stage                                      |
| stageId     | String       | True | stage id                                              |

### Example

- request example

  ```
  GET /PenguinStats/api/stage
  ```

- return example

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

### return

Please refer to [HTTP Status Codes](https://httpstatuses.com/).

## Stage-id（deprecated）

Father interface: [Stage](#Stage)

### description:

An interface for reading dropping at specified stage.

- open to external usage (Penguin-Statistics API)
- chapter of stages is not included
- dropping info of stages is not included
- **father interface is recommended**

* ~

### URI

```
GET /PenguinStats/api/stage/{stageId}
```

**Parameters**

| Parameters | Type   | Must | Description |
| ---------- | ------ | ---- | ----------- |
| stageId    | String | True | stage id    |

### Response Message

#### response Parameters

| Parameters  | Type         | Description                                           |
| ----------- | ------------ | ----------------------------------------------------- |
| specialDrop | List(Dict)   | set of special drop items id                          |
| extraDrop   | List(Dict)   | set of extra drop items id                            |
| code        | String       | stage code                                            |
| stageType   | String       | stage type, restricted by [type values](#type values) |
| normalDrop  | List(Dict)   | set of common drop items id                           |
| zoneId      | String       | chapter id                                            |
| dropsSet    | List(String) | set of drop items id                                  |
| apCost      | int          | ap cost of stage                                      |
| stageId     | String       | stage id                                              |

**specialDrop, extraDrop, normalDrop Parameters**

| Parameters | Type   | Must | Description                                                  |
| ---------- | ------ | ---- | ------------------------------------------------------------ |
| itemId     | String | True | drop item id                                                 |
| itemType   | String | True | drop item type, restricted by [itemType values](#itemType values) |
| sortId     | int    | True | drop item sorted id                                          |
| name       | String | True | drop item name                                               |
| iconUrl    | url    | True | drop item icon uri                                           |
| rarity     | int    | True | rarity of drop item                                          |

### Examples

- request example

  ```
  GET /PenguinStats/api/stage/main_04-06
  ```

- return example

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

### return

Please refer to [HTTP Status Codes](https://httpstatuses.com/).

## Item

### description:

An interface for reading item.

- open to external usage (Penguin-Statistics API)
- currently only all items are available, specified item is not supported
- ~

### URI

```
GET  PenguinStats/api/items
```

### Response Message

#### response Parameters

| Parameters | Type       | Description                                             |
| ---------- | ---------- | ------------------------------------------------------- |
| items      | List(Dict) | a list containing dropping info of all chapters' stages |

**items Parameters**

| Parameters | Type   | Must | Description                                                  |
| ---------- | ------ | ---- | ------------------------------------------------------------ |
| itemId     | String | True | drop item id                                                 |
| itemType   | String | True | drop item type, restricted by [itemType values](#itemType values) |
| sortId     | int    | True | drop item sorted id                                          |
| name       | String | True | drop item name                                               |
| iconUrl    | url    | True | drop item icon uri                                           |
| rarity     | int    | True | ty of drop item                                              |

### Example

- request example

  ```
  GET  PenguinStats/api/items
  ```

- return example

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

### return

Please refer to [HTTP Status Codes](https://httpstatuses.com/).

## Limitation

### description:

An interface for reading bound of drop items at stage.

- currently drop could be 0
- currently bounds of all stages are available, specified stage/item is not supported
- ~

### URI

```
GET /PenguinStats/api/limitation
```

### Response Message

#### response Parameters

| Parameters  | Type       | Description                          |
| ----------- | ---------- | ------------------------------------ |
| limitations | List(Dict) | set of restricted info of drop items |

**limitations Parameters**

| Parameters         | Type       | Must | Description                    |
| ------------------ | ---------- | ---- | ------------------------------ |
| itemQuantityBounds | List(Dict) | True | bounds of stage drop items     |
| name               | String     | True | stage id                       |
| itemTypeBounds     | List(Dict) | True | bounds of stage drop item type |

**itemQuantityBounds Parameters**

| Parameters | Type       | Must | Description                |
| ---------- | ---------- | ---- | -------------------------- |
| bounds     | List(Dict) | True | bounds of stage drop items |
| itemId     | String     | True | drop item id               |

**itemTypeBounds Parameters**

| Parameters | Type       | Must | Description                    |
| ---------- | ---------- | ---- | ------------------------------ |
| bounds     | List(Dict) | True | bounds of stage drop item type |

**bounds Parameters**

| Parameters | Type | Must | Description |
| ---------- | ---- | ---- | ----------- |
| lower      | int  | True | lower bound |
| upper      | int  | True | upper bound |

### Example

- request example

  ```
  GET /PenguinStats/api/limitation
  ```

- return example

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

### return

Please refer to [HTTP Status Codes](https://httpstatuses.com/).

## User

### description:

An interface for user related activities. 

- User Login
- ~~Normal user sign in~~
- Internal User sign in
- ~

### URI

```
POST /PenguinStats/api/users
```

### Request Message

#### request Parameters

| Parameters | Type   | Must | Description |
| ---------- | ------ | ---- | ----------- |
| userID     | String | True | user id     |

### Response Message

#### response Parameters

| Parameters | Type         | Description                                 |
| ---------- | ------------ | ------------------------------------------- |
| createTime | long         | user created time, format is UNIX timestamp |
| userID     | String       | user id                                     |
| ips        | List(String) | set of user used ip                         |
| tags       | List(String) | set of user labels                          |

### Example

- request example

  ```
  {userID:"15748941"}
  ```

- return example

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

### return

Please refer to [HTTP Status Codes](https://httpstatuses.com/).

## Appendix

### source values

| Value                  | Description                                |
| ---------------------- | ------------------------------------------ |
| penguin-stats.io       | Upload by users from penguin-stats.io      |
| arknights-drop-sniffer | Upload by penguin-stats auto-upload script |
| etc.                   |                                            |

### type values

| Value                  | Description                   |
| ---------------------- | ----------------------------- |
| MAINLINE               | Main Story stages/chapters    |
| arknights-drop-sniffer | Daily Mission stages/chapters |
| ACTIVITY               | Event stages/chapters         |

### itemType values

| Value    | Description   |
| -------- | ------------- |
| CARD_EXP | battle record |
| MATERIAL | material      |
| FURN     | furniture     |



**Some return examples are cut down due to length.**