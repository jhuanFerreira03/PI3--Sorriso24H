/*
 * Import function triggers from their respective submodules:
 *
 * import {onCall} from "firebase-functions/v2/https";
 * import {onDocumentWritten} from "firebase-functions/v2/firestore";
 *
 * See a full list of supported triggers at https://firebase.google.com/docs/functions
 */
import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

const app = admin.initializeApp();
const db = app.firestore();

const collectionUsers = db.collection("users");
const region = "southamerica-east1";

type User = {
  uid: string;
  nome: string;
  email: string;
  telefone: string;
};

type Response = {
  status: string | unknown;
  message: string | unknown;
  payload: unknown
};
// Start writing functions
// https://firebase.google.com/docs/functions/typescript


export const addUsers = functions
  .region(region).https
  .onCall(async (data: User, context) => {
    const Res:Response = {
      status: "",
      message: "",
      payload: undefined,
    };
    if (data.nome != undefined &&
      data.email != undefined &&
      data.telefone != undefined &&
      data.uid != undefined) {
      try {
        await app.firestore().collection("Users").add(data);
        Res.status = "Ok";
        Res.message = "Aparentemente foi";
        return Res;
      } catch (error) {
        Res.status = "Erro";
        Res.message = "Aparentemente não foi";
        return Res;
      }
    } else {
      return Res;
    }
  });

export const addUser = functions
  .region(region).https
  .onRequest(async (req, res) => {
    try {
      const docRef = await collectionUsers.add(req._read);
      res.send("user add R: " + docRef.id);
    } catch (error) {
      functions.logger.error("Erro ao inserir");
      res.send("Erro ao inserir");
    }
  });

export const deleteUser = functions.region(region)
  .https.onRequest( async (req, res) => {
    const docId = "x1Q2gRhf2ODpX3dfBLB5";
    await collectionUsers.doc(docId).delete();
    res.send("Deve ter excluido");
  });


export const cadastrarUsuario = functions
  .region(region)
  .runWith({enforceAppCheck: false})
  .https.onCall(async (data: User, context) => {
    const cResponse: Response = {
      status: "ERROR",
      message: "Dados não fornecidos ou incompletos",
      payload: undefined,
    };

    if (data.nome != undefined &&
      data.email != undefined &&
      data.telefone != undefined &&
      data.uid != undefined) {
      try {
        const docRef = await app.firestore().collection("users").add(data);
        cResponse.status = "SUCCESS";
        cResponse.message = "Usuário cadastrado com sucesso!";
        cResponse.payload = {uid: docRef.id};
      } catch (e: any) {
        functions.logger.error("Erro ao incluir perfil:", data.email);
        functions.logger.error("Exception: ", e.message);
        cResponse.status = "ERROR";
        cResponse.message = "Erro ao cadastrar usuário - Verificar Logs";
        cResponse.payload = null;
      }
    } else {
      cResponse.status = "ERROR";
      cResponse.message = "Perfil faltando informações";
      cResponse.payload = undefined;
    }
    return cResponse;
  });

export const salvarDadosDoUsuario = functions
  .region(region)
  .runWith({enforceAppCheck: false})
  .https.onCall(async (data: any, context: any) => {
    const cResponse: Response = {
      status: "ERROR",
      message: "Dados não fornecidos ou incompletos",
      payload: undefined,
    };

    // Verifica se o objeto de usuário foi fornecido
    const usuario = data as User;
    if (data.nome != undefined &&
      data.email != undefined &&
      data.telefone != undefined &&
      data.uid != undefined) {
      try {
        // Cria uma coleção userData e adiciona o documento do usuário a ela
        const docRef = await app
          .firestore()
          .collection("userData")
          .add(usuario);
        cResponse.status = "SUCCESS";
        cResponse.message = "Dados do usuário salvos com sucesso!";
        cResponse.payload = {docId: docRef.id};
      } catch (e: any) {
        functions.logger.error(
          "Erro ao salvar dados do usuário:",
          usuario.email
        );
        functions.logger.error("Exception: ", e.message);
        cResponse.status = "ERROR";
        cResponse.message = "Erro ao salvar dados do usuário - Verificar Logs";
        cResponse.payload = null;
      }
    } else {
      cResponse.status = "ERROR";
      cResponse.message = "Perfil faltando informações";
      cResponse.payload = undefined;
    }

    return cResponse;
  });
