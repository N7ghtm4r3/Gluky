# Gluky's dataset

> [!NOTE]  
> All the data have been random generated and are not related to anyone

To use the original dataset you have to simply follow these steps:

- Create the database if not created
- Insert an user
- **Manually change the `id` value with this one: `4794a3c834214306aee8b6a5816e597a`**
- Insert the [measurements](measurements.sql) table, be sure to disable the `foreign keys check` to avoid problem
- Then insert the [meals](meals.sql) table
- Insert the last table [basal_insulin_records](basal_insulin_records.sql) table

The dataset has data from the first of the month to the tenth of the month, in particular the months available
are `March`, `April` and `May`

## Table naming convention

```bash
#(a for march, b for april and c for may)
l = convetion letter 
```

### measurements

Example: 1 March 2025

|  id   | creation_date | daily_notes | afternoon_snack | basal_insulin |  breakfast   |  dinner   |  lunch   | morning_snack |              owner               |                                                                   
|:-----:|:-------------:|:-----------:|:---------------:|:-------------:|:------------:|:---------:|:--------:|:-------------:|:--------------------------------:|
| mMMdd |   timestamp   |             |   afternoon_l   |    b_mMMdd    | breakfast_l  | dinner_l  | lunch_l  |   morning_l   |             owner_id             |
| m0301 | 1740787200000 |             |  afternoon_1a   |    b_m0301    | breakfast_1a | dinner_1a | lunch_1a |  morning_1a   | 4794a3c834214306aee8b6a5816e597a |

### meals

```bash
#(such 1, 2, etc)
d = the number of the day
```

Example: 1 March 2025

|     id      | annotation_date | glycemia | insulin_units | post_prandial_glycemia |         raw_content         |   type    | measurement_id |                                                                    
|:-----------:|:---------------:|:--------:|:-------------:|------------------------|:---------------------------:|:---------:|:--------------:|
| mealType_dl |    timestamp    |   int    |      int      | int                    |            text             |   enum    |      text      | 
|    m0301    |  1740787200000  |   110    |       3       | 125                    | {"pane":80,"marmellata":20} | BREAKFAST |     m0301      |

### basal_insulin_records

```bash
#(such 1, 2, etc)
d = the number of the day
```

Example: 1 March 2025

|     id      | annotation_date | glycemia | insulin_units | measurement_id |                                                                    
|:-----------:|:---------------:|:--------:|:-------------:|:--------------:|
| mealType_dl |    timestamp    |   int    |      int      |      text      | 
|    m0301    |  1740787200000  |   110    |       3       |     m0301      | 