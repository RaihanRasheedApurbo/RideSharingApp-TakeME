const mongoose = require('mongoose');

const connectDB = async () => {
    try{
        // mongodb connection string
        const uri = process.env.MONGO_URI || "mongodb+srv://takeMe:takeMe@cluster0.u1lfn.mongodb.net/takeMeDB?retryWrites=true&w=majority";
        const con = await mongoose.connect(uri, {
            useNewUrlParser: true,
            useUnifiedTopology: true,
            useFindAndModify: false,
            useCreateIndex: true
        })

        console.log(`MongoDB connected : ${con.connection.host}`);
    }catch(err){
        console.log(err);
        process.exit(1);
    }
}

module.exports = connectDB