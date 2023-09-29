import * as mongoose from "mongoose";

const faceSchema = new mongoose.Schema({
    eventID: {
        type: String,
        required: true,
    },
    sectionID: {
        type: String,
        required: true,
    },
    row: {
        type: String,
        required: true,
    },
    seat: {
        type: String,
        required: true,
    },
    label: {
        type: String,
        required: true,
    },
    descriptions: {
        type: Array,
        required: true,
    },
} , { "strict": false });

faceSchema.index({ eventID: 1, sectionID: 1, row: 1, seat: 1 }, { unique: true });

// Mongoose#model(name, [schema], [collection], [skipInit])
module.exports = mongoose.model("Face", faceSchema, "Face");
