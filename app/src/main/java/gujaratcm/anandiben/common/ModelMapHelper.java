package gujaratcm.anandiben.common;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import gujaratcm.anandiben.CMApplication;
import gujaratcm.anandiben.activerecordbase.ActiveRecordBase;
import gujaratcm.anandiben.activerecordbase.ActiveRecordException;

public class ModelMapHelper<T extends ActiveRecordBase> {

    Class<T> type = null;

    public T getObject(Class<T> cls, JSONObject jobj) {
        type = cls;
        List<Field> fields = getColumnFields();

        T entity = getInstance();
        for (Field field : fields) {
            ModelMapper mapper = field.getAnnotation(ModelMapper.class);
            if (mapper != null) {
                field.setAccessible(true);
                String key = mapper.JsonKey();
                boolean isArray = mapper.IsArray();
                if (isArray) {
                    ArrayList<String> arr = getStringArray(jobj, key);
                    try {
                        field.set(entity, arr);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                Object val = jobj.opt(key);
                if (val != null) {
                    try {
                        entity = setField(field, entity, val.toString());
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return entity;

    }

    public T getObject(Class<T> cls, T existing, JSONObject jobj) {
        type = cls;
        List<Field> fields = getColumnFields();
        T entity = existing;
        for (Field field : fields) {
            ModelMapper mapper = field.getAnnotation(ModelMapper.class);
            if (mapper != null) {
                field.setAccessible(true);
                String key = mapper.JsonKey();
                if (jobj.has(key)) {
                    boolean isArray = mapper.IsArray();
                    if (isArray) {
                        ArrayList<String> arr = getStringArray(jobj, key);
                        try {
                            field.set(entity, arr);
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        continue;
                    }
                    Object val = jobj.opt(key);
                    if (val != null) {
                        try {
                            entity = setField(field, entity, val.toString());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        } catch (IllegalArgumentException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return entity;

    }

    private ArrayList<String> getStringArray(JSONObject jobj, String key) {

        ArrayList<String> arr = new ArrayList<String>();
        try {
            JSONArray jarr = jobj.getJSONArray(key);
            for (int i = 0; i < jarr.length(); i++) {
                String val = jarr.getString(i);
                arr.add(val);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return arr;

    }

    private T setField(Field field, T entity, String content)
            throws NumberFormatException, IllegalArgumentException,
            IllegalAccessException {
        if (field.getType() == long.class) {
            if (content != null) {
                field.setLong(entity, parseLong(content));
            }
        } else if (field.getType() == String.class) {
            field.set(entity, content);
        } else if (field.getType() == int.class) {
            if (content != null) {

                field.setInt(entity, parseInt(content));
            }
        } else if (field.getType() == Double.class) {
            if (content != null) {
                field.setDouble(entity, parseDouble(content));
            }
        } else if (field.getType() == boolean.class) {
            boolean val = false;
            if (content.equalsIgnoreCase("yes")) {
                val = true;
            }
            if (content.equalsIgnoreCase("1")) {
                val = true;
            }
            if (content.equalsIgnoreCase("true")) {
                val = true;
            }
            field.setBoolean(entity, val);
        }
        return entity;

    }

    private T getInstance() {
        T entity = null;
        try {
            if (this.type.getSuperclass() == ActiveRecordBase.class) {
                entity = CMApplication.Connection().newEntity(type);
            } else {
                entity = this.type.newInstance();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ActiveRecordException e) {
            e.printStackTrace();
        }
        return entity;
    }

    protected List<Field> getColumnFields() {
        Field[] fields = this.type.getDeclaredFields();
        List<Field> columns = new ArrayList<Field>();
        for (Field field : fields) {
            columns.add(field);
        }
        return columns;
    }

    public String getUniqueFieldName(Class<T> cls) {
        type = cls;
        List<Field> fields = getColumnFields();
        for (Field field : fields) {
            ModelMapper mapper = field.getAnnotation(ModelMapper.class);
            if (mapper != null) {
                boolean isUnique = mapper.IsUnique();
                if (isUnique) {
                    return field.getName();
                }
            }

        }
        return "_id";
    }

    public Object getUniqueFieldValue(Class<T> cls, T obj) {
        type = cls;
        List<Field> fields = getColumnFields();
        for (Field field : fields) {
            ModelMapper mapper = field.getAnnotation(ModelMapper.class);
            if (mapper != null) {
                boolean isUnique = mapper.IsUnique();
                if (isUnique) {
                    try {
                        return field.get(obj);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return "";
    }

    private int parseInt(String val) {
        if (val.length() <= 0) {
            return 0;
        }
        int intval = 0;
        try {
            intval = Integer.parseInt(val);
        } catch (Exception e) {
            Utils.LogException(e);
        }
        return intval;
    }

    private double parseDouble(String val) {
        double d = 0;
        try {
            d = Double.parseDouble(val);
        } catch (Exception e) {
            Utils.LogException(e);
        }
        return d;
    }

    private long parseLong(String val) {
        long d = 0;
        try {
            d = Long.parseLong(val);
        } catch (Exception e) {
            Utils.LogException(e);
        }
        return d;
    }

}
