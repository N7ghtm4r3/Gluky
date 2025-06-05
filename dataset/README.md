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

### measurements

| id | creation_date | daily_notes | afternoon_snack | basal_insulin | breakfast | dinner | lunch | morning_snack | owner |                                                                   
|----|---------------|-------------|-----------------|---------------|-----------|--------|-------|---------------|-------|