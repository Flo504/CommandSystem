package fr.flo504.commandsystem.command;

public class CommandTag {

    private final Object value;

    public CommandTag(Object value) {
        this.value = value;
    }

    public boolean asBoolean(){
        return value != null && value instanceof Boolean && (boolean) value;
    }

    public int asInt(){
        return value == null || !(value instanceof Integer) ? -1 : (int) value;
    }

    public double asDouble(){
        return value == null || !(value instanceof Double) ? -1 : (double) value;
    }

    public long asLong(){
        return value == null || !(value instanceof Long) ? -1 : (long) value;
    }

    public float asFloat(){
        return value == null || !(value instanceof Float) ? -1 : (float) value;
    }

    public String asString(){
        return value == null || !(value instanceof String) ? "" : (String) value;
    }

    public Object get(){
        return value;
    }

}
