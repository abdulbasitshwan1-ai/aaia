package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.ActionType
import com.example.data.model.MessageSender
import com.example.data.model.TaskType
import com.example.data.model.game.CarRarity
import com.example.data.model.game.MissionType
import com.example.data.model.game.PaintFinish
import com.example.data.model.game.SetupPreset
import com.example.data.model.game.TransmissionType

class Converters {
    @TypeConverter
    fun fromMessageSender(value: MessageSender): String = value.name

    @TypeConverter
    fun toMessageSender(value: String): MessageSender = try {
        MessageSender.valueOf(value)
    } catch (e: Exception) {
        MessageSender.BASOKA
    }

    @TypeConverter
    fun fromActionType(value: ActionType): String = value.name

    @TypeConverter
    fun toActionType(value: String): ActionType = try {
        ActionType.valueOf(value)
    } catch (e: Exception) {
        ActionType.NONE
    }

    @TypeConverter
    fun fromTaskType(value: TaskType): String = value.name

    @TypeConverter
    fun toTaskType(value: String): TaskType = try {
        TaskType.valueOf(value)
    } catch (e: Exception) {
        TaskType.REMINDER
    }

    @TypeConverter
    fun fromCarRarity(value: CarRarity): String = value.name

    @TypeConverter
    fun toCarRarity(value: String): CarRarity = try {
        CarRarity.valueOf(value)
    } catch (e: Exception) {
        CarRarity.COMMON
    }

    @TypeConverter
    fun fromPaintFinish(value: PaintFinish): String = value.name

    @TypeConverter
    fun toPaintFinish(value: String): PaintFinish = try {
        PaintFinish.valueOf(value)
    } catch (e: Exception) {
        PaintFinish.GLOSS
    }

    @TypeConverter
    fun fromTransmissionType(value: TransmissionType): String = value.name

    @TypeConverter
    fun toTransmissionType(value: String): TransmissionType = try {
        TransmissionType.valueOf(value)
    } catch (e: Exception) {
        TransmissionType.AUTOMATIC
    }

    @TypeConverter
    fun fromSetupPreset(value: SetupPreset): String = value.name

    @TypeConverter
    fun toSetupPreset(value: String): SetupPreset = try {
        SetupPreset.valueOf(value)
    } catch (e: Exception) {
        SetupPreset.BALANCED
    }

    @TypeConverter
    fun fromMissionType(value: MissionType): String = value.name

    @TypeConverter
    fun toMissionType(value: String): MissionType = try {
        MissionType.valueOf(value)
    } catch (e: Exception) {
        MissionType.STREET_RACE
    }
}
